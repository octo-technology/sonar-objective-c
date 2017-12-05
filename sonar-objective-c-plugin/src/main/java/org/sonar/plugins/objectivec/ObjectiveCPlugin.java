/**
 * backelite-sonar-objective-c-plugin - Enables analysis of Objective-C projects into SonarQube.
 * Copyright © 2012 OCTO Technology, Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonar.plugins.objectivec;

import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.objectivec.complexity.LizardSensor;
import org.sonar.plugins.objectivec.surefire.SurefireSensor;
import org.sonar.plugins.objectivec.violations.ObjectiveCProfile;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasProfile;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasRulesDefinition;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasSensor;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfile;
import org.sonar.plugins.objectivec.violations.oclint.OCLintRulesDefinition;
import org.sonar.plugins.objectivec.violations.oclint.OCLintSensor;
import org.sonar.plugins.objectivec.coverage.CoberturaSensor;
import org.sonar.plugins.objectivec.colorizer.ObjectiveCColorizerFormat;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.objectivec.cpd.ObjectiveCCpdMapping;

import com.google.common.collect.ImmutableList;

import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasProfileImporter;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfileImporter;

@Properties({
        @Property(key = CoberturaSensor.REPORT_PATTERN_KEY, defaultValue = CoberturaSensor.DEFAULT_REPORT_PATTERN, name = "Path to unit test coverage report(s)", description = "Relative to projects' root. Ant patterns are accepted", global = false, project = true),
        @Property(key = OCLintSensor.REPORT_PATH_KEY, defaultValue = OCLintSensor.DEFAULT_REPORT_PATH, name = "Path to oclint pmd formatted report", description = "Relative to projects' root.", global = false, project = true),
        @Property(key = FauxPasSensor.REPORT_PATH_KEY, defaultValue = FauxPasSensor.DEFAULT_REPORT_PATH, name = "Path to fauxpas json formatted report", description = "Relative to projects' root.", global = false, project = true),
        @Property(key = LizardSensor.REPORT_PATH_KEY, defaultValue = LizardSensor.DEFAULT_REPORT_PATH, name = "Path to lizard report", description = "Relative to projects' root.", global = false, project = true)
})
public class ObjectiveCPlugin extends SonarPlugin {

    public List getExtensions() {
        return ImmutableList.of(ObjectiveC.class,

                ObjectiveCCpdMapping.class,

                ObjectiveCSquidSensor.class,
                ObjectiveCProfile.class,
                SurefireSensor.class,
                CoberturaSensor.class,

                OCLintRulesDefinition.class,
                OCLintSensor.class,
                OCLintProfile.class,
                OCLintProfileImporter.class,

                FauxPasSensor.class,
                FauxPasRulesDefinition.class,
                FauxPasProfile.class,
                FauxPasProfileImporter.class,

                LizardSensor.class
                );
    }

    // Global Objective C constants
    public static final String FALSE = "false";

    public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";
    public static final String FILE_SUFFIXES_DEFVALUE = "h,m,mm";

    public static final String PROPERTY_PREFIX = "sonar.objectivec";

    public static final String TEST_FRAMEWORK_KEY = PROPERTY_PREFIX + ".testframework";
    public static final String TEST_FRAMEWORK_DEFAULT = "ghunit";

}
