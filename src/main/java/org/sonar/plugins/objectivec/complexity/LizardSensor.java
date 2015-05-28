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
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by agilherr on 28/05/15.
 */
@SuppressWarnings("deprecation")
public class LizardSensor implements Sensor {

    public static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".lizard.report";
    public static final String DEFAULT_REPORT_PATH = "sonar-reports/lizard-report.xml";

    private final Settings conf;
    private final FileSystem fileSystem;

    public LizardSensor(final FileSystem moduleFileSystem, final Settings config) {
        this.conf = config;
        this.fileSystem = moduleFileSystem;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return project.isRoot() && fileSystem.languages().contains(ObjectiveC.KEY);
    }

    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        final String projectBaseDir = project.getFileSystem().getBasedir().getPath();
        Map<String, List<Measure>> measures = parseReportsIn(projectBaseDir, new LizardReportParser());

        for (Map.Entry<String, List<Measure>> entry: measures.entrySet()) {
            String[] filePath = entry.getKey().split("/");
            LoggerFactory.getLogger(getClass()).info("{}", filePath[filePath.length - 1]);
            for (Measure measure : entry.getValue()) {
                LoggerFactory.getLogger(getClass()).info("  {} = {}", measure.getMetric().getName(), measure.getValue());
            }
        }

        new LizardMeasurePersistor(project, sensorContext).saveMeasures(measures);
    }

    //key = file name,
    private Map<String, List<Measure>> parseReportsIn(final String baseDir, LizardReportParser parser) {
        final StringBuilder reportFileName = new StringBuilder(baseDir);
        reportFileName.append("/").append(reportPath());
        LoggerFactory.getLogger(getClass()).info("Processing complexity report ");
        return parser.parseReport(new File(reportFileName.toString()));
    }

    private String reportPath() {
        String reportPath = conf.getString(REPORT_PATH_KEY);
        if (reportPath == null) {
            reportPath = DEFAULT_REPORT_PATH;
        }
        return reportPath;
    }
}
