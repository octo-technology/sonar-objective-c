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

import java.io.File;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser.XmlStreamHandler;

final class OCLintXMLStreamHandler implements XmlStreamHandler {
    private static final int PMD_MINIMUM_PRIORITY = 5;
    private final Collection<Violation> foundViolations;
    private final Project project;
    private final SensorContext context;

    public OCLintXMLStreamHandler(final Collection<Violation> violations,
            final Project p, final SensorContext c) {
        foundViolations = violations;
        project = p;
        context = c;
    }

    public void stream(final SMHierarchicCursor rootCursor)
            throws XMLStreamException {
        final SMInputCursor file = rootCursor.advance().childElementCursor(
                "file");

        while (null != file.getNext()) {
            collectViolationsFor(file);
        }
    }

    private void collectViolationsFor(final SMInputCursor file)
            throws XMLStreamException {
        final String filePath = file.getAttrValue("name");
        LoggerFactory.getLogger(getClass()).debug(
                "Collection violations for {}", filePath);
        final org.sonar.api.resources.File resource = findResource(filePath);
        if (fileExists(resource)) {
            LoggerFactory.getLogger(getClass()).debug(
                    "File {} was found in the project.", filePath);
            collectFileViolations(resource, file);
        }
    }

    private org.sonar.api.resources.File findResource(final String filePath) {
        return org.sonar.api.resources.File.fromIOFile(new File(filePath),
                project);
    }

    private void collectFileViolations(
            final org.sonar.api.resources.File resource,
            final SMInputCursor file) throws XMLStreamException {
        final SMInputCursor line = file.childElementCursor("violation");

        while (null != line.getNext()) {
            recordViolation(resource, line);
        }
    }

    private void recordViolation(final org.sonar.api.resources.File resource,
            final SMInputCursor line) throws XMLStreamException {
        final Rule rule = Rule.create();
        final Violation violation = Violation.create(rule, resource);

        // PMD Priorities are 1, 2, 3, 4, 5 RulePriority[0] is INFO
        rule.setSeverity(RulePriority.values()[PMD_MINIMUM_PRIORITY
                - Integer.valueOf(line.getAttrValue("priority"))]);
        rule.setKey(line.getAttrValue("rule"));
        rule.setRepositoryKey(OCLintRuleRepository.REPOSITORY_KEY);

        violation.setLineId(Integer.valueOf(line.getAttrValue("beginline")));

        violation.setMessage(line.getElemStringValue());

        foundViolations.add(violation);
    }

    private boolean fileExists(final org.sonar.api.resources.File file) {
        return context.getResource(file) != null;
    }

}
