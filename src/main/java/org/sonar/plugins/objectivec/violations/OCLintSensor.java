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
package org.sonar.plugins.objectivec.violations;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.PathResolver;

import java.io.File;

public final class OCLintSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintSensor.class);

    public static final String REPORT_PATH_KEY = "sonar.objectivec.oclint.reportPath";

    private final FileSystem fileSystem;
    private final PathResolver pathResolver;
    private final ResourcePerspectives resourcePerspectives;
    private final Settings settings;

    public OCLintSensor(final FileSystem fileSystem, final PathResolver pathResolver,
            final ResourcePerspectives resourcePerspectives, final Settings settings) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.resourcePerspectives = resourcePerspectives;
        this.settings = settings;
    }

    public boolean shouldExecuteOnProject(final Project project) {
        return StringUtils.isNotEmpty(settings.getString(REPORT_PATH_KEY));
    }

    public void analyse(final Project project, final SensorContext context) {
        String path = settings.getString(REPORT_PATH_KEY);
        File report = pathResolver.relativeFile(fileSystem.baseDir(), path);

        if (!report.isFile()) {
            LOGGER.warn("OCLint report not found at {}", report);
            return;
        }

        LOGGER.info("parsing {}", report);
        OCLintParser.parseReport(report, project, context, resourcePerspectives);
    }

    @Override
    public String toString() {
        return "Objective-C OCLint Sensor";
    }
}
