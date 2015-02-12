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
package org.sonar.plugins.objectivec.violations.fauxpas;

import org.apache.commons.io.IOUtils;
import org.codehaus.staxmate.in.SMInputCursor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.objectivec.violations.oclint.OCLintRuleRepository;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by gillesgrousset on 12/02/15.
 */
public class FauxPasReportParser {

    private final Project project;
    private final SensorContext context;

    private static final Logger LOGGER = LoggerFactory.getLogger(FauxPasReportParser.class);

    public FauxPasReportParser(final Project p, final SensorContext c) {
        project = p;
        context = c;
    }

    public Collection<Violation> parseReport(File reportFile) {

        final Collection<Violation> violations = new ArrayList<Violation>();

        try {
            // Read and parse report
            FileReader fr = new FileReader(reportFile);
            Object reportObj = JSONValue.parse(fr);
            IOUtils.closeQuietly(fr);

            // Record violations
            if (reportObj != null) {

                JSONObject reportJson = (JSONObject)reportObj;
                JSONArray diagnosticsJson = (JSONArray)reportJson.get("diagnostics");

                for (Object obj : diagnosticsJson) {
                    recordViolation((JSONObject)obj, violations);

                }
            }

        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to parse FauxPas report file", e);
        }


        return violations;
    }

    private void recordViolation(final JSONObject diagnosticJson, Collection<Violation> violations) {

        String filePath = (String)diagnosticJson.get("file");

        if (filePath != null) {

            org.sonar.api.resources.File resource = org.sonar.api.resources.File.fromIOFile(new File(filePath), project);

            final Rule rule = Rule.create();
            final Violation violation = Violation.create(rule, resource);

            rule.setRepositoryKey(FauxPasRuleRepository.REPOSITORY_KEY);
            rule.setKey((String)diagnosticJson.get("ruleShortName"));

            violation.setMessage((String)diagnosticJson.get("info"));


            JSONObject extent = (JSONObject)diagnosticJson.get("extent");
            JSONObject start = (JSONObject)extent.get("start");

            int lineNumber = Integer.parseInt(start.get("line").toString());
            if (lineNumber > 0) {
                violation.setLineId(Integer.parseInt(start.get("line").toString()));
            }

            violations.add(violation);

        }

    }

}
