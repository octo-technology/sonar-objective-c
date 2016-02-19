/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite
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

import java.io.File;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser.XmlStreamHandler;
import org.sonar.plugins.objectivec.violations.fauxpas.FauxPasRulesDefinition;

final class OCLintXMLStreamHandler implements XmlStreamHandler {
    private static final int PMD_MINIMUM_PRIORITY = 5;
    private final Project project;
    private final SensorContext context;
    private final ResourcePerspectives resourcePerspectives;

    public OCLintXMLStreamHandler(final Project p, final SensorContext c, final ResourcePerspectives resourcePerspectives) {
        project = p;
        context = c;
        this.resourcePerspectives = resourcePerspectives;
    }

    public void stream(final SMHierarchicCursor rootCursor) throws XMLStreamException {

        final SMInputCursor file = rootCursor.advance().childElementCursor("file");
        while (null != file.getNext()) {
            collectIssuesFor(file);
        }
    }

    private void collectIssuesFor(final SMInputCursor file) throws XMLStreamException {

        final String filePath = file.getAttrValue("name");
        LoggerFactory.getLogger(getClass()).debug("Collection violations for {}", filePath);
        final org.sonar.api.resources.File resource = findResource(filePath);
        if (fileExists(resource)) {
            LoggerFactory.getLogger(getClass()).debug("File {} was found in the project.", filePath);
            collectFileIssues(resource, file);
        }
    }

    private void collectFileIssues(final org.sonar.api.resources.File resource, final SMInputCursor file) throws XMLStreamException {

        final SMInputCursor line = file.childElementCursor("violation");

        while (null != line.getNext()) {
            recordViolation(resource, line);
        }
    }

    private org.sonar.api.resources.File findResource(final String filePath) {
        return org.sonar.api.resources.File.fromIOFile(new File(filePath), project);
    }

    private void recordViolation(final org.sonar.api.resources.File resource, final SMInputCursor line) throws XMLStreamException {

        Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

        if (issuable != null) {

            Issue issue = issuable.newIssueBuilder()
                    .ruleKey(RuleKey.of(FauxPasRulesDefinition.REPOSITORY_KEY, line.getAttrValue("rule")))
                    .line(Integer.valueOf(line.getAttrValue("beginline")))
                    .message(line.getElemStringValue())
                    .build();

            issuable.addIssue(issue);


        }
    }

    private boolean fileExists(final org.sonar.api.resources.File file) {
        return context.getResource(file) != null;
    }

}
