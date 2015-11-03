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
package org.sonar.plugins.objectivec;

import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.objectivec.clang.ClangRulesDefinition;
import org.sonar.plugins.objectivec.clang.ClangSensor;
import org.sonar.plugins.objectivec.cobertura.CoberturaSensor;
import org.sonar.plugins.objectivec.lizard.LizardSensor;
import org.sonar.plugins.objectivec.oclint.OCLintProfile;
import org.sonar.plugins.objectivec.oclint.OCLintProfileImporter;
import org.sonar.plugins.objectivec.oclint.OCLintRulesDefinition;
import org.sonar.plugins.objectivec.oclint.OCLintSensor;
import org.sonar.plugins.objectivec.surefire.SurefireSensor;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveCPlugin extends SonarPlugin {
    public List getExtensions() {
        List<Object> extensions = new ArrayList<Object>();

        extensions.add(ObjectiveC.class);
        extensions.add(PropertyDefinition.builder(ObjectiveC.FILE_SUFFIXES_KEY)
                .defaultValue(ObjectiveC.DEFAULT_FILE_SUFFIXES)
                .name("File suffixes")
                .description("Comma-separated list of suffixes for files to analyze. To not filter, leave the list empty.")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        extensions.add(ObjectiveCColorizerFormat.class);
        extensions.add(ObjectiveCCpdMapping.class);

        extensions.add(ObjectiveCSquidSensor.class);
        extensions.add(ObjectiveCProfile.class);

        extensions.add(ClangRulesDefinition.class);
        extensions.add(ClangSensor.class);
        extensions.add(PropertyDefinition.builder(ClangSensor.REPORTS_PATH_KEY)
                .name("Clang Static Analyzer Reports")
                .description("Path to the directory containing all the *.plist Clang report files. The path may be absolute or relative to the project base directory.")
                .subCategory("Clang")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        extensions.add(CoberturaSensor.class);
        extensions.add(PropertyDefinition.builder(CoberturaSensor.REPORT_PATH_KEY)
                .name("Report path")
                .description("Path (absolute or relative) to Cobertura XML report file.")
                .subCategory("Cobertura")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        extensions.add(LizardSensor.class);
        extensions.add(PropertyDefinition.builder(LizardSensor.REPORT_PATH_KEY)
                .name("Report path")
                .description("Path (absolute or relative) to Lizard XML report file.")
                .subCategory("Complexity")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        extensions.add(OCLintRulesDefinition.class);
        extensions.add(OCLintSensor.class);
        extensions.add(OCLintProfile.class);
        extensions.add(OCLintProfileImporter.class);
        extensions.add(PropertyDefinition.builder(OCLintSensor.REPORT_PATH_KEY)
                .name("Report path")
                .description("Path (absolute or relative) to OCLint PMD formatted XML report file.")
                .subCategory("OCLint")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        extensions.add(SurefireSensor.class);
        extensions.add(PropertyDefinition.builder(SurefireSensor.REPORTS_PATH_KEY)
                .name("JUnit Reports")
                .description("Path to the directory containing all the *.xml JUnit report files. The path may be absolute or relative to the project base directory.<br /><br />"
                        + "Extra logic has been added to search your test sources for each classname that is defined in the JUnit report.<br /><br />"
                        + "Classes will attempt to match the pattern: <tt>**/${classname}.m</tt>")
                .subCategory("JUnit")
                .onQualifiers(Qualifiers.PROJECT)
                .build());

        return extensions;
    }

}
