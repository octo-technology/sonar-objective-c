/*
 * SonarQube Objective-C (Community) Plugin
 * Copyright (C) 2012-2016 OCTO Technology, Backelite, and contributors
 * mailto:sonarqube@googlegroups.com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.objectivec.oclint;

import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

import javax.xml.stream.XMLStreamException;
import java.io.File;

final class OCLintParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintParser.class);

    private final Project project;
    private final SensorContext context;
    private final ResourcePerspectives resourcePerspectives;

    private OCLintParser(final Project project, final SensorContext context,
            final ResourcePerspectives resourcePerspectives) {
        this.project = project;
        this.context = context;
        this.resourcePerspectives = resourcePerspectives;
    }

    public static void parseReport(File xmlFile, Project project, SensorContext context,
            ResourcePerspectives resourcePerspectives) {
        new OCLintParser(project, context, resourcePerspectives).parse(xmlFile);
    }


    private void parse(File xmlFile) {
        try {
            StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {
                @Override
                public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
                    rootCursor.advance();
                    collectFiles(rootCursor.childElementCursor("file"));
                }
            });
            parser.parse(xmlFile);
        } catch (XMLStreamException e) {
            throw new XmlParserException(e);
        }
    }

    private void collectFiles(final SMInputCursor file) throws XMLStreamException {
        while (null != file.getNext()) {
            final String filePath = file.getAttrValue("name");
            LOGGER.debug("Collecting issues for {}", filePath);

            final org.sonar.api.resources.File resource = org.sonar.api.resources.File.fromIOFile(new File(filePath), project);

            if (context.getResource(resource) != null) {
                LOGGER.debug("File {} was found in the project.", filePath);
                collectFileIssues(resource, file);
            }
        }
    }

    private void collectFileIssues(final org.sonar.api.resources.File resource,
            final SMInputCursor file) throws XMLStreamException {
        final SMInputCursor line = file.childElementCursor("violation");

        while (line.getNext() != null) {
            recordIssue(resource, line);
        }
    }

    private void recordIssue(final org.sonar.api.resources.File resource,
            final SMInputCursor line) throws XMLStreamException {
        Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

        if (issuable != null) {
            Issue issue = issuable.newIssueBuilder()
                    .ruleKey(RuleKey.of(OCLintRulesDefinition.REPOSITORY_KEY, line.getAttrValue("rule")))
                    .line(Integer.valueOf(line.getAttrValue("beginline")))
                    .message(line.getElemStringValue())
                    .build();

            issuable.addIssue(issue);
        }
    }
}
