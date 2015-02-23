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
package org.sonar.plugins.objectivec.violations.oclint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser;

public class OCLintXMLStreamHandlerTest {
	private static final String EMPTY_REPORT = "<pmd version=\"violations-0.8dev\"></pmd>";
	private static final String DESCRIPTION = "TEST DESCRIPTION";
	private static final Integer VIOLATION_LINE = Integer.valueOf(99);
	private static final String RULE_KEY = "TEST RULE";
	private static final String VALID_REPORT = "<pmd version=\"violations-0.8dev\"><file name=\"dummy/test\"><violation beginline=\"" + VIOLATION_LINE + "\" endline=\"19\" begincolumn=\"13\" endcolumn=\"20\" rule=\"" + RULE_KEY + "\" ruleset=\"Basic Rules\" package=\"org.sprunck.bee\" class=\"Bee\" method=\"toString\" externalInfoUrl=\"http://pmd.sourceforge.net/rules/basic.html#UselessOperationOnImmutable\" priority=\"3\">" + DESCRIPTION +  "</violation></file></pmd>";
	private ProjectBuilder projectBuilder;

	@Test
	public void streamLeavesTheCollectionEmptyWhenNoLinesAreFound() throws XMLStreamException {
		final Collection<Violation> parseResults = new ArrayList<Violation>();
		final StaxParser parser = new StaxParser(new OCLintXMLStreamHandler(parseResults, null, null));

		parser.parse(new StringInputStream(EMPTY_REPORT));

		assertTrue(parseResults.isEmpty());
	}

	@Test
	public void streamAddAviolationForALineInTheReport() throws XMLStreamException {
		final org.sonar.api.resources.File dummyFile = new org.sonar.api.resources.File("test");
		givenAProject().containingSourceDirectory("dummy");
		final SensorContext context = mock(SensorContext.class);

		final Collection<Violation> parseResults = new ArrayList<Violation>();
		final StaxParser parser = new StaxParser(new OCLintXMLStreamHandler(parseResults, project(), context));

		when(context.getResource(any(Resource.class))).thenReturn(dummyFile);

		parser.parse(new StringInputStream(VALID_REPORT));

		assertFalse(parseResults.isEmpty());
	}

	private Project project() {
        Project project = givenAProject().project();
        ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
        project.setFileSystem(fileSystem);
        when(fileSystem.getBasedir()).thenReturn(new File("."));
		return project;
	}

	private ProjectBuilder givenAProject() {
		projectBuilder = new ProjectBuilder();
		return projectBuilder;
	}

}
