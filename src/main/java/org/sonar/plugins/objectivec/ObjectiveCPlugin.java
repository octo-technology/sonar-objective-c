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

import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.objectivec.coverage.CoberturaSensor;
import org.sonar.plugins.objectivec.colorizer.ObjectiveCColorizerFormat;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.objectivec.core.ObjectiveCSourceImporter;
import org.sonar.plugins.objectivec.cpd.ObjectiveCCpdMapping;

import com.google.common.collect.ImmutableList;

import org.sonar.plugins.objectivec.tests.SurefireSensor;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasProfile;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasProfileImporter;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasRuleRepository;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasSensor;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfile;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfileImporter;
import org.sonar.plugins.objectivec.violations.oclint.OCLintRuleRepository;
import org.sonar.plugins.objectivec.violations.oclint.OCLintSensor;

@Properties({
        @Property(key = CoberturaSensor.REPORT_PATTERN_KEY, defaultValue = CoberturaSensor.DEFAULT_REPORT_PATTERN, name = "Path to unit test coverage report(s)", description = "Relative to projects' root. Ant patterns are accepted", global = false, project = true),
        @Property(key = OCLintSensor.REPORT_PATH_KEY, defaultValue = OCLintSensor.DEFAULT_REPORT_PATH, name = "Path to fauxpas pmd formatted report", description = "Relative to projects' root.", global = false, project = true)
})
public class ObjectiveCPlugin extends SonarPlugin {

    public List<Class<? extends Extension>> getExtensions() {
        return ImmutableList.of(ObjectiveC.class,
                ObjectiveCSourceImporter.class,
                ObjectiveCColorizerFormat.class,
                ObjectiveCCpdMapping.class,

                ObjectiveCSquidSensor.class,
                ObjectiveCProfile.class,
                SurefireSensor.class,
                CoberturaSensor.class,

                OCLintRuleRepository.class,
                OCLintSensor.class,
                OCLintProfile.class,
                OCLintProfileImporter.class,

                FauxPasSensor.class,
                FauxPasRuleRepository.class,
                FauxPasProfile.class,
                FauxPasProfileImporter.class
                );
    }

    // Global Objective C constants
    public static final String FALSE = "false";

    public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";
    public static final String FILE_SUFFIXES_DEFVALUE = "h,m";

    public static final String PROPERTY_PREFIX = "sonar.objectivec";

    public static final String TEST_FRAMEWORK_KEY = PROPERTY_PREFIX + ".testframework";
    public static final String TEST_FRAMEWORK_DEFAULT = "ghunit";

}
