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
package org.sonar.plugins.objectivec.complexity;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.plugins.objectivec.ObjectiveC;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This sensor searches for the report generated from the tool Lizard
 * in order to save complexity metrics.
 *
 * @author Andres Gil Herrera
 * @since 28/05/15
 */
public final class LizardSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LizardSensor.class);

    public static final String REPORT_PATH_KEY = "sonar.objectivec.lizard.reportPath";

    private final FileSystem fileSystem;
    private final PathResolver pathResolver;
    private final Settings settings;

    public LizardSensor(final FileSystem fileSystem, final PathResolver pathResolver, final Settings settings) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.settings = settings;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return StringUtils.isNotEmpty(settings.getString(REPORT_PATH_KEY))
                && fileSystem.languages().contains(ObjectiveC.KEY);
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        String path = settings.getString(REPORT_PATH_KEY);
        File report = pathResolver.relativeFile(fileSystem.baseDir(), path);

        if (!report.isFile()) {
            LOGGER.warn("Lizard report not found at {}", report);
            return;
        }

        LOGGER.info("parsing {}", report);
        Map<String, List<Measure>> measures = LizardReportParser.parseReport(report);

        if (measures == null) {
            return;
        }

        LOGGER.info("Saving results of complexity analysis");
        saveMeasures(project, context, measures);
    }

    private void saveMeasures(Project project, SensorContext context, final Map<String, List<Measure>> measures) {
        for (Map.Entry<String, List<Measure>> entry : measures.entrySet()) {
            final org.sonar.api.resources.File file =
                    org.sonar.api.resources.File.fromIOFile(new File(entry.getKey()), project);

            if (context.getResource(file) != null) {
                for (Measure measure : entry.getValue()) {
                    try {
                        LOGGER.debug("Save measure {} for file {}", measure.getMetric().getName(), file);
                        context.saveMeasure(file, measure);
                    } catch (Exception e) {
                        LOGGER.error(" Exception -> {} -> {}", entry.getKey(), measure.getMetric().getName());
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Objective-C Lizard Sensor";
    }
}
