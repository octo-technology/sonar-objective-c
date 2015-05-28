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
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by agilherr on 28/05/15.
 */
@SuppressWarnings("deprecation")
public class LizardMeasurePersistor {

    private Project project;
    private SensorContext sensorContext;
    private DecoratorContext decoratorContext;
    private FileSystem fileSystem;

    public LizardMeasurePersistor(final Project p, final DecoratorContext c) {
        this.project = p;
        this.decoratorContext = c;
        this.fileSystem = fileSystem;
    }

    public LizardMeasurePersistor(final Project p, final SensorContext c, final FileSystem fileSystem) {
        this.project = p;
        this.sensorContext = c;
        this.fileSystem = fileSystem;
    }

    public void saveMeasures(final Map<String, List<Measure>> measures) {
/*
        for (InputFile file : fileSystem.inputFiles(fileSystem.predicates().all())) {
            //LoggerFactory.getLogger(getClass()).info("Inputfile {} \n {}", file.absolutePath(), file.relativePath());
            for (Map.Entry<String, List<Measure>> entry : measures.entrySet()){
                String path = entry.getKey();
                String name = file.relativePath();
                if (path.equalsIgnoreCase(name)){
                    //LoggerFactory.getLogger(getClass()).info("Save Measures for inputfile");
                    for (Measure measure : entry.getValue()){
                        try {
                            sensorContext.saveMeasure(file, measure);
                        } catch (Exception e) {
                            LoggerFactory.getLogger(getClass()).error(" Exception -> {} -> {} \n {}", entry.getKey(), measure.getMetric().getName(), e.getMessage());
                        }
                    }
                    break;
                }
            }
        }

        */
        for (Map.Entry<String, List<Measure>> entry : measures.entrySet()) {
            final org.sonar.api.resources.File objcfile = org.sonar.api.resources.File.fromIOFile(new File(project.getFileSystem().getBasedir(), entry.getKey()), project);
            if (fileExists(sensorContext, objcfile)) {
                for (Measure measure : entry.getValue()) {
                    try {
                        sensorContext.saveMeasure(objcfile, measure);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error(" Exception -> {} -> {}", entry.getKey(), measure.getMetric().getName());
                    }
                }
            }
        }

    }

    private boolean fileExists(final SensorContext context,
                               final org.sonar.api.resources.File file) {
        return context.getResource(file) != null;
    }
}
