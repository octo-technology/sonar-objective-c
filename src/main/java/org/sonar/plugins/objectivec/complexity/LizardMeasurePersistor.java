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

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This class is used to save the measures created by the lizardReportParser in the sonar database
 *
 * @author Andres Gil Herrera
 * @since 28/05/15.
 */
public class LizardMeasurePersistor {

    private Project project;
    private SensorContext sensorContext;

    public LizardMeasurePersistor(final Project p, final SensorContext c) {
        this.project = p;
        this.sensorContext = c;
    }

    /**
     *
     * @param measures Map containing as key the name of the file and as value a list containing the measures for that file
     */
    public void saveMeasures(final Map<String, List<Measure>> measures) {
        for (Map.Entry<String, List<Measure>> entry : measures.entrySet()) {
            final org.sonar.api.resources.File objcfile = org.sonar.api.resources.File.fromIOFile(new File(project.getFileSystem().getBasedir(), entry.getKey()), project);
            if (fileExists(sensorContext, objcfile)) {
                for (Measure measure : entry.getValue()) {
                    try {
                        LoggerFactory.getLogger(getClass()).debug("Save measure {} for file {}", measure.getMetric().getName(), objcfile);
                        sensorContext.saveMeasure(objcfile, measure);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error(" Exception -> {} -> {}", entry.getKey(), measure.getMetric().getName());
                    }
                }
            }
        }
    }

    /**
     *
     * @param context context of the sensor
     * @param file file to prove for
     * @return true if the resource is indexed and false if not
     */
    private boolean fileExists(final SensorContext context,
                               final org.sonar.api.resources.File file) {
        return context.getResource(file) != null;
    }
}
