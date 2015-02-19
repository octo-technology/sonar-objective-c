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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser;

final class OCLintParser {
    private final Project project;
    private final SensorContext context;

    public OCLintParser(final Project p, final SensorContext c) {
        project = p;
        context = c;
    }

    public Collection<Violation> parseReport(final File file) {
        Collection<Violation> result;
        try {
            final InputStream reportStream = new FileInputStream(file);
            result = parseReport(reportStream);
            reportStream.close();
        } catch (final IOException e) {
            LoggerFactory.getLogger(getClass()).error(
                    "Error processing file named {}", file, e);
            result = new ArrayList<Violation>();
        }
        return result;
    }

    public Collection<Violation> parseReport(final InputStream inputStream) {
        final Collection<Violation> violations = new ArrayList<Violation>();
        try {
            final StaxParser parser = new StaxParser(
                    new OCLintXMLStreamHandler(violations, project, context));
            parser.parse(inputStream);
            LoggerFactory.getLogger(getClass()).error(
                    "Reporting {} violations.", violations.size());
        } catch (final XMLStreamException e) {
            LoggerFactory.getLogger(getClass()).error(
                    "Error while parsing XML stream.", e);
        }
        return violations;
    }

}
