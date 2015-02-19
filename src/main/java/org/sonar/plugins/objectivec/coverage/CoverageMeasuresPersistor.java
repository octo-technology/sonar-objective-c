/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite
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
package org.sonar.plugins.objectivec.coverage;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.utils.PathUtils;

final class CoverageMeasuresPersistor {
    private final Project project;
    private final SensorContext context;

    public CoverageMeasuresPersistor(final Project p, final SensorContext c) {
        project = p;
        context = c;
    }

    public void saveMeasures(final Map<String, CoverageMeasuresBuilder> coverageMeasures) {

        for (final Map.Entry<String, CoverageMeasuresBuilder> entry : coverageMeasures.entrySet()) {
            saveMeasuresForFile(entry.getValue(), entry.getKey());
        }
    }

    private void saveMeasuresForFile(final CoverageMeasuresBuilder measureBuilder, final String filePath) {

        LoggerFactory.getLogger(getClass()).debug("Saving measures for {}", filePath);
        final org.sonar.api.resources.File objcfile = org.sonar.api.resources.File.fromIOFile(new File(project.getFileSystem().getBasedir(), filePath), project);

        if (fileExists(context, objcfile)) {
            LoggerFactory.getLogger(getClass()).debug(
                    "File {} was found in the project.", filePath);
            saveMeasures(measureBuilder, objcfile);
        }
    }

    private void saveMeasures(final CoverageMeasuresBuilder measureBuilder,
            final org.sonar.api.resources.File objcfile) {
        for (final Measure measure : measureBuilder.createMeasures()) {
            LoggerFactory.getLogger(getClass()).debug("Measure {}",
                    measure.getMetric().getName());
            context.saveMeasure(objcfile, measure);
        }
    }

    private boolean fileExists(final SensorContext context,
            final org.sonar.api.resources.File file) {
        return context.getResource(file) != null;
    }
}
