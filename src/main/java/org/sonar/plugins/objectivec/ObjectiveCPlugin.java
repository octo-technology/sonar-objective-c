/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 François Helg, Cyril Picat and OCTO Technology
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

import org.sonar.api.SonarPlugin;
import org.sonar.plugins.objectivec.colorizer.ObjectiveCColorizerFormat;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.objectivec.core.ObjectiveCSourceImporter;
import org.sonar.plugins.objectivec.cpd.ObjectiveCCpdMapping;

import com.google.common.collect.ImmutableList;

public class ObjectiveCPlugin extends SonarPlugin {

	public List getExtensions() {
		return ImmutableList.of(
        ObjectiveC.class,
        ObjectiveCSourceImporter.class,
        ObjectiveCColorizerFormat.class,
        ObjectiveCCpdMapping.class,

        ObjectiveCSquidSensor.class
//        ObjectiveCRuleRepository.class,
//        ObjectiveCProfile.class,
//
//        OCTestDriverSurefireSensor.class,
//        OCTestDriverCoverageSensor.class,
//
//        OCTestMavenInitializer.class,
//        OCTestMavenPluginHandler.class,
//        OCTestCoverageSensor.class,
//        OCTestSurefireSensor.class
        );
	}

//Global JavaScript constants
	public static final String FALSE = "false";

	public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";
	public static final String FILE_SUFFIXES_DEFVALUE = "h,m";

  public static final String PROPERTY_PREFIX = "sonar.objectivec";

  public static final String TEST_FRAMEWORK_KEY = PROPERTY_PREFIX + ".testframework";
  public static final String TEST_FRAMEWORK_DEFAULT = "ghunit";

}
