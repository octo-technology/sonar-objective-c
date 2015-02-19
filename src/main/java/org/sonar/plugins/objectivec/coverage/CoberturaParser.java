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
package org.sonar.plugins.objectivec.coverage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.utils.StaxParser;

final class CoberturaParser {
    public Map<String, CoverageMeasuresBuilder> parseReport(final File xmlFile) {
        Map<String, CoverageMeasuresBuilder> result = null;
        try {
            final InputStream reportStream = new FileInputStream(xmlFile);
            result = parseReport(reportStream);
            reportStream.close();
        } catch (final IOException e) {
            LoggerFactory.getLogger(getClass()).error(
                    "Error processing file named {}", xmlFile, e);
            result = new HashMap<String, CoverageMeasuresBuilder>();
        }
        return result;
    }

    public Map<String, CoverageMeasuresBuilder> parseReport(
            final InputStream xmlFile) {

        final Map<String, CoverageMeasuresBuilder> measuresForReport = new HashMap<String, CoverageMeasuresBuilder>();
        try {
            final StaxParser parser = new StaxParser(
                    new CoberturaXMLStreamHandler(measuresForReport));
            parser.parse(xmlFile);
        } catch (final XMLStreamException e) {
            LoggerFactory.getLogger(getClass()).error(
                    "Error while parsing XML stream.", e);
        }
        return measuresForReport;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
