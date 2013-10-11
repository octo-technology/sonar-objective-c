w/*
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;

public class OCLintParserTest {
	private final String VALID_REPORT = "<pmd version=\"oclint-0.8dev\"><file name=\"/dummy/TEST_FILE\"><violation beginline=\"19\" endline=\"19\" begincolumn=\"13\" endcolumn=\"20\" rule=\"UselessOperationOnImmutable\" ruleset=\"Basic Rules\" package=\"org.sprunck.bee\" class=\"Bee\" method=\"toString\" externalInfoUrl=\"http://pmd.sourceforge.net/rules/basic.html#UselessOperationOnImmutable\" priority=\"3\">An operation on an Immutable object (String, BigDecimal or BigInteger) won't change the object itself</violation></file></pmd>";

	@Test
	public void parseReportShouldReturnAnEmptyCollectionWhenTheReportIsInvalid() {
		final OCLintParser testedParser = new OCLintParser(null, null);
		final Collection<Violation> violations = testedParser.parseReport(new StringInputStream(""));

		assertTrue(violations.isEmpty());
	}

	@Test
	public void parseReportShouldReturnAnEmptyMapWhenTheFileIsInvalid() {
		final OCLintParser testedParser = new OCLintParser(null, null);
		final Collection<Violation> violations = testedParser.parseReport(new File(""));

		assertTrue(violations.isEmpty());
	}

	@Test
	public void parseReportShouldReturnACollectionOfViolationsWhenTheReportIsNotEmpty() {
		final Project project = new Project("Test");
		final org.sonar.api.resources.File dummyFile = new org.sonar.api.resources.File("dummy/test");
		final SensorContext context = mock(SensorContext.class);
		final ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		final List<File> sourceDirs = new ArrayList<File>();

		final OCLintParser testedParser = new OCLintParser(project, context);

		sourceDirs.add(new File("/dummy"));
		when(fileSystem.getSourceDirs()).thenReturn(sourceDirs);
		when(context.getResource(any(Resource.class))).thenReturn(dummyFile);
		project.setFileSystem(fileSystem);

		final Collection<Violation> violations = testedParser.parseReport(new StringInputStream(VALID_REPORT));
		assertFalse(violations.isEmpty());
	}


}
