/*
 * SonarQube Objective-C (Community) Plugin
 * Copyright (C) 2012-2016 OCTO Technology, Backelite, and contributors
 * mailto:sonarqube@googlegroups.com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.objectivec.lizard;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.plugins.objectivec.api.ObjectiveC;

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
    private final ResourcePerspectives resourcePerspectives;
    private final RulesProfile rulesProfile;
    private final Settings settings;

    public LizardSensor(final FileSystem fileSystem, final PathResolver pathResolver,
            final ResourcePerspectives resourcePerspectives, final RulesProfile rulesProfile, final Settings settings) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.resourcePerspectives = resourcePerspectives;
        this.rulesProfile = rulesProfile;
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
        Map<String, List<Measure>> measures = LizardReportParser.parseReport(fileSystem, resourcePerspectives,
                rulesProfile, context, report);

        if (measures == null) {
            return;
        }

        LOGGER.info("Saving results of complexity analysis");
        saveMeasures(context, measures);
    }

    private void saveMeasures(SensorContext context, final Map<String, List<Measure>> measures) {
        for (Map.Entry<String, List<Measure>> entry : measures.entrySet()) {
            final InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasPath(entry.getKey()));
            final Resource resource = inputFile == null ? null : context.getResource(inputFile);

            if (resource != null) {
                for (Measure measure : entry.getValue()) {
                    LOGGER.debug("Save measure {} for file {}", measure.getMetric().getName(), resource.getPath());
                    context.saveMeasure(resource, measure);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Objective-C Lizard Sensor";
    }
}
