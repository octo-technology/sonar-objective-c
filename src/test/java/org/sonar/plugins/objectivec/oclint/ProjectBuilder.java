/*
 * SonarQube Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite, SonarSource,
 *             Denis Bregeon, Mete Balci, Andrés Gil Herrera, Matthew DeTullio
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.objectivec.oclint;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

final class ProjectBuilder {
	private final Project project = new Project("Test");
	private final ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
	private final List<File> sourceDirs = new ArrayList<File>();

	public ProjectBuilder() {
		project.setFileSystem(fileSystem);
		when(fileSystem.getSourceDirs()).thenReturn(sourceDirs);
        when(fileSystem.getBasedir()).thenReturn(new File("."));
	}

	public Project project() {
		return project;
	}

	public void containingSourceDirectory(final String d) {
		sourceDirs.add(new File(d));
	}
}
