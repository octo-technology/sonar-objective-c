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
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.utils.StaxParser;

public final class CoberturaParser {
	public void parseReport(final File xmlFile,
			final Map<String, CoverageMeasuresBuilder> coverageData)
			throws XMLStreamException {

		final StaxParser parser = new StaxParser(
				new StaxParser.XmlStreamHandler() {
					/**
					 * {@inheritDoc}
					 */
					public void stream(final SMHierarchicCursor rootCursor)
							throws XMLStreamException {
						rootCursor.advance();
						collectPackageMeasures(
								rootCursor.descendantElementCursor("package"),
								coverageData);
					}
				});
		parser.parse(xmlFile);
	}

	private void collectPackageMeasures(final SMInputCursor pack,
			final Map<String, CoverageMeasuresBuilder> coverageData)
			throws XMLStreamException {
		while (pack.getNext() != null) {
			collectFileMeasures(pack.descendantElementCursor("class"),
					coverageData);
		}
	}

	private void collectFileMeasures(final SMInputCursor clazz,
			final Map<String, CoverageMeasuresBuilder> coverageData)
			throws XMLStreamException {
		while (clazz.getNext() != null) {
			final String fileName = clazz.getAttrValue("filename");
			CoverageMeasuresBuilder builder = coverageData.get(fileName);
			if (builder == null) {
				builder = CoverageMeasuresBuilder.create();
				coverageData.put(fileName, builder);
			}
			collectFileData(clazz, builder);
		}
	}

	private void collectFileData(final SMInputCursor clazz,
			final CoverageMeasuresBuilder builder) throws XMLStreamException {
		final SMInputCursor line = clazz.childElementCursor("lines").advance()
				.childElementCursor("line");
		while (line.getNext() != null) {
			final int lineId = Integer.parseInt(line.getAttrValue("number"));
			long noHits = Long.parseLong(line.getAttrValue("hits"));
			if (noHits > Integer.MAX_VALUE) {
				noHits = Integer.MAX_VALUE;
			}
			builder.setHits(lineId, (int) noHits);

			final String isBranch = line.getAttrValue("branch");
			final String text = line.getAttrValue("condition-coverage");
			if (StringUtils.equals(isBranch, "true")
					&& StringUtils.isNotBlank(text)) {
				final String[] conditions = StringUtils.split(
						StringUtils.substringBetween(text, "(", ")"), "/");
				builder.setConditions(lineId, Integer.parseInt(conditions[1]),
						Integer.parseInt(conditions[0]));
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
