/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.objectivec.surefire;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;
import org.sonar.plugins.objectivec.ObjectiveC;
import org.sonar.plugins.surefire.TestCaseDetails;
import org.sonar.plugins.surefire.TestSuiteParser;
import org.sonar.plugins.surefire.TestSuiteReport;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurefireParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurefireParser.class);

    private final Project project;
    private final SensorContext context;
    private final FileSystem fileSystem;

    public SurefireParser(Project project, SensorContext context, FileSystem fileSystem) {
        this.project = project;
        this.context = context;
        this.fileSystem = fileSystem;
    }

    public void collect(File reportsDir) {
        File[] xmlFiles = getReports(reportsDir);

        if (xmlFiles.length == 0) {
            insertZeroWhenNoReports();
        } else {
            parseFiles(xmlFiles);
        }
    }

    private File[] getReports(File dir) {
        if (!dir.isDirectory() || !dir.exists()) {
            return new File[0];
        }

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("TEST") && name.endsWith(".xml");
            }
        });
    }

    private void insertZeroWhenNoReports() {
        context.saveMeasure(CoreMetrics.TESTS, 0.0);
    }

    private void parseFiles(File[] reports) {
        Set<TestSuiteReport> analyzedReports = new HashSet<TestSuiteReport>();
        try {
            for (File report : reports) {
                TestSuiteParser parserHandler = new TestSuiteParser();
                StaxParser parser = new StaxParser(parserHandler, false);
                parser.parse(report);

                for (TestSuiteReport fileReport : parserHandler.getParsedReports()) {
                    if (!fileReport.isValid() || analyzedReports.contains(fileReport)) {
                        continue;
                    }
                    if (fileReport.getTests() > 0) {
                        double testsCount = fileReport.getTests() - fileReport.getSkipped();
                        saveClassMeasure(fileReport, CoreMetrics.SKIPPED_TESTS, fileReport.getSkipped());
                        saveClassMeasure(fileReport, CoreMetrics.TESTS, testsCount);
                        saveClassMeasure(fileReport, CoreMetrics.TEST_ERRORS, fileReport.getErrors());
                        saveClassMeasure(fileReport, CoreMetrics.TEST_FAILURES, fileReport.getFailures());
                        saveClassMeasure(fileReport, CoreMetrics.TEST_EXECUTION_TIME, fileReport.getTimeMS());
                        double passedTests = testsCount - fileReport.getErrors() - fileReport.getFailures();
                        if (testsCount > 0) {
                            double percentage = passedTests * 100d / testsCount;
                            saveClassMeasure(fileReport, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage));
                        }
                        saveTestsDetails(fileReport);
                        analyzedReports.add(fileReport);
                    }
                }
            }

        } catch (Exception e) {
            throw new XmlParserException("Can not parse surefire reports", e);
        }
    }

    private void saveTestsDetails(TestSuiteReport fileReport) throws TransformerException {
        StringBuilder testCaseDetails = new StringBuilder(256);
        testCaseDetails.append("<tests-details>");
        List<TestCaseDetails> details = fileReport.getDetails();
        for (TestCaseDetails detail : details) {
            testCaseDetails.append("<testcase status=\"").append(detail.getStatus())
                    .append("\" time=\"").append(detail.getTimeMS())
                    .append("\" name=\"").append(detail.getName()).append("\"");
            boolean isError = detail.getStatus().equals(TestCaseDetails.STATUS_ERROR);
            if (isError || detail.getStatus().equals(TestCaseDetails.STATUS_FAILURE)) {
                testCaseDetails.append(">")
                        .append(isError ? "<error message=\"" : "<failure message=\"")
                        .append(StringEscapeUtils.escapeXml(detail.getErrorMessage())).append("\">")
                        .append("<![CDATA[").append(StringEscapeUtils.escapeXml(detail.getStackTrace())).append("]]>")
                        .append(isError ? "</error>" : "</failure>").append("</testcase>");
            } else {
                testCaseDetails.append("/>");
            }
        }
        testCaseDetails.append("</tests-details>");
        context.saveMeasure(getUnitTestResource(fileReport.getClassKey()), new Measure(CoreMetrics.TEST_DATA, testCaseDetails.toString()));
    }

    private void saveClassMeasure(TestSuiteReport fileReport, Metric metric, double value) {
        if (!Double.isNaN(value)) {

            String className = fileReport.getClassKey();

            context.saveMeasure(getUnitTestResource(className), metric, value);

            // Try with + in name
            try {
                context.saveMeasure(getUnitTestResource(className.replace('_', '+')), metric, value);
            } catch (Exception e) {
                // no-op - file was probably already registered successfully
            }
        }
    }

    public Resource getUnitTestResource(String classname) {
        String fileName = classname.replace('.', '/') + ".m";

        File file = new File(fileName);
        if (!file.isAbsolute()) {
            file = new File(fileSystem.baseDir(), fileName);
        }

        /*
         * Most xcodebuild JUnit parsers don't include the path to the class in the class field, so search for if it
         * wasn't found in the root.
         */
        if (!file.isFile() || !file.exists()) {
            List<File> files = ImmutableList.copyOf(fileSystem.files(fileSystem.predicates().and(
                    fileSystem.predicates().hasLanguage(ObjectiveC.KEY),
                    fileSystem.predicates().hasType(InputFile.Type.TEST),
                    fileSystem.predicates().matchesPathPattern("**/" + fileName))));

            if (files.isEmpty()) {
                LOGGER.info("Unable to locate test source file {}", fileName);
            } else {
                /*
                 * Lazily get the first file, since we wouldn't be able to determine the correct one from just the
                 * test class name in the event that there are multiple matches.
                 */
                file = files.get(0);
            }
        }

        org.sonar.api.resources.File sonarFile = org.sonar.api.resources.File.fromIOFile(file, project);
        sonarFile.setQualifier(Qualifiers.UNIT_TEST_FILE);
        return sonarFile;
    }
}
