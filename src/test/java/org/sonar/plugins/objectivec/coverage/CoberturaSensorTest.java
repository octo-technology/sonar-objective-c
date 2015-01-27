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
package org.sonar.plugins.objectivec.coverage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.util.SortedSet;
import java.util.TreeSet;

public final class CoberturaSensorTest {

    private Settings settings;

    @Before
    public void setUp() {
        settings = new Settings();
    }

	@Test
	public void shouldExecuteOnProjectShouldBeTrueWhenProjectIsObjc() {
		final Project project = new Project("Test");

        FileSystem fileSystem = mock(FileSystem.class);
        SortedSet<String> languages = new TreeSet<String>();
        languages.add(ObjectiveC.KEY);
        when(fileSystem.languages()).thenReturn(languages);

		final CoberturaSensor testedSensor = new CoberturaSensor(fileSystem, settings);

		assertTrue(testedSensor.shouldExecuteOnProject(project));
	}

	@Test
	public void shouldExecuteOnProjectShouldBeFalseWhenProjectIsSomethingElse() {
		final Project project = new Project("Test");
        settings.setProperty("sonar.language", "Test");

        FileSystem fileSystem = mock(FileSystem.class);
        SortedSet<String> languages = new TreeSet<String>();
        languages.add("Test");
        when(fileSystem.languages()).thenReturn(languages);

		final CoberturaSensor testedSensor = new CoberturaSensor(fileSystem, settings);

		assertFalse(testedSensor.shouldExecuteOnProject(project));
	}

}
