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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.resources.Project;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public final class ObjectiveCCoverageSensor implements Sensor {
    public static final String REPORT_PATTERN_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".coverage.reportPattern";
    public static final String DEFAULT_REPORT_PATTERN = "coverage-reports/coverage-*.xml";

    private final ReportFilesFinder reportFilesFinder;
    private final CoberturaParser parser = new CoberturaParser();

    public ObjectiveCCoverageSensor() {
        this(null);
    }

    public ObjectiveCCoverageSensor(final Settings config) {
        reportFilesFinder = new ReportFilesFinder(config, REPORT_PATTERN_KEY,
                DEFAULT_REPORT_PATTERN);
    }

    public boolean shouldExecuteOnProject(final Project project) {
        return ObjectiveC.KEY.equals(project.getLanguageKey());
    }

    public void analyse(final Project project, final SensorContext context) {
        final CoverageMeasuresPersistor measuresPersistor = new CoverageMeasuresPersistor(
                project, context);
        final String projectBaseDir = project.getFileSystem().getBasedir()
                .getPath();

        measuresPersistor.saveMeasures(parseReportsIn(projectBaseDir));
    }

    private Map<String, CoverageMeasuresBuilder> parseReportsIn(
            final String baseDir) {
        final Map<String, CoverageMeasuresBuilder> measuresTotal = new HashMap<String, CoverageMeasuresBuilder>();

        for (final File report : reportFilesFinder.reportsIn(baseDir)) {
            LoggerFactory.getLogger(getClass()).info(
                    "Processing coverage report {}", report);
            measuresTotal.putAll(parser.parseReport(report));
        }

        return measuresTotal;
    }

}
