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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public final class OCLintSensorTest {

	@Test
	public void shouldExecuteOnProjectShouldBeTrueWhenProjectIsObjc() {
		final Project project = new Project("Test");
		final OCLintSensor testedSensor = new OCLintSensor();
		final PropertiesConfiguration config = new PropertiesConfiguration();
		config.addProperty("sonar.language", ObjectiveC.KEY);
		project.setConfiguration(config);

		assertTrue(testedSensor.shouldExecuteOnProject(project));
	}

	@Test
	public void shouldExecuteOnProjectShouldBeFalseWhenProjectIsSomethingElse() {
		final Project project = new Project("Test");
		final OCLintSensor testedSensor = new OCLintSensor();
		final PropertiesConfiguration config = new PropertiesConfiguration();
		config.addProperty("sonar.language", "Test");
		project.setConfiguration(config);

		assertFalse(testedSensor.shouldExecuteOnProject(project));
	}

}
