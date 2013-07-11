/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 François Helg, Cyril Picat and OCTO Technology
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
package org.sonar.plugins.objectivec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public final class ObjectiveCCoverageSensor implements Sensor {
	public static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
			+ ".coverage.reportPath";
	public static final String DEFAULT_REPORT_PATH = "coverage-reports/coverage-*.xml";
	private static final int UNIT_TEST_COVERAGE = 0;

	private final Settings conf;
	private final CoberturaParser parser = new CoberturaParser();

	public ObjectiveCCoverageSensor() {
		this(null);
	}

	public ObjectiveCCoverageSensor(final Settings config) {
		this(null, config);
	}

	public ObjectiveCCoverageSensor(final RuleFinder rules,
			final Settings config) {
		conf = config;
	}

	public boolean shouldExecuteOnProject(final Project project) {
		return ObjectiveC.KEY.equals(project.getLanguageKey());
	}

	public void analyse(final Project project, final SensorContext context) {
		final List<File> reports = getReports(conf, project.getFileSystem()
				.getBasedir().getPath(), REPORT_PATH_KEY, DEFAULT_REPORT_PATH);

		final Map<String, CoverageMeasuresBuilder> coverageMeasures = parseReports(reports);
		saveMeasures(project, context, coverageMeasures, UNIT_TEST_COVERAGE);
	}

	private Map<String, CoverageMeasuresBuilder> parseReports(
			final List<File> reports) {
		final Map<String, CoverageMeasuresBuilder> measuresTotal = new HashMap<String, CoverageMeasuresBuilder>();
		final Map<String, CoverageMeasuresBuilder> measuresForReport = new HashMap<String, CoverageMeasuresBuilder>();

		for (final File report : reports) {
			LoggerFactory.getLogger(getClass()).info("parsing {}", report);
			try {
				measuresForReport.clear();
				parser.parseReport(report, measuresForReport);

				if (!measuresForReport.isEmpty()) {
					measuresTotal.putAll(measuresForReport);
					break;
				}
			} catch (final XMLStreamException e) {
				e.printStackTrace();
			}
		}

		return measuresTotal;
	}

	private void saveMeasures(final Project project,
			final SensorContext context,
			final Map<String, CoverageMeasuresBuilder> coverageMeasures,
			final int coveragetype) {
		for (final Map.Entry<String, CoverageMeasuresBuilder> entry : coverageMeasures
				.entrySet()) {
			final String filePath = entry.getKey();
			LoggerFactory.getLogger(getClass()).info("Saving measures for {}", filePath);
			final org.sonar.api.resources.File objcfile = org.sonar.api.resources.File
					.fromIOFile(new File(filePath), project);
			if (fileExist(context, objcfile)) {
				LoggerFactory.getLogger(getClass()).info("File found {}", objcfile);
				for (final Measure measure : entry.getValue().createMeasures()) {
					LoggerFactory.getLogger(getClass()).info("Measure {}", measure);
					context.saveMeasure(objcfile, measure);
				}
			}
		}
	}

	private boolean fileExist(final SensorContext context,
			final org.sonar.api.resources.File file) {
		return context.getResource(file) != null;
	}

	private List<File> getReports(final Settings conf,
			final String baseDirPath, final String reportPathPropertyKey,
			final String defaultReportPath) {
		String reportPath = conf.getString(reportPathPropertyKey);
		if (reportPath == null) {
			reportPath = defaultReportPath;
		}

		final DirectoryScanner scanner = new DirectoryScanner();
		final String[] includes = new String[1];
		includes[0] = reportPath;
		scanner.setIncludes(includes);
		scanner.setBasedir(new File(baseDirPath));
		scanner.scan();
		final String[] relPaths = scanner.getIncludedFiles();

		final List<File> reports = new ArrayList<File>();
		for (final String relPath : relPaths) {
			reports.add(new File(baseDirPath, relPath));
		}

		return reports;
	}

}
