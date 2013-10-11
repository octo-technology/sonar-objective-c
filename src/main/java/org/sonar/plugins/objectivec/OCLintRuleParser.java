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
package org.sonar.plugins.objectivec;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;

/**
 * Largely copied from AndroidLint's equivalent class whose authors are Stephane
 * Nicolas and Jerome Van Der Linden according to the class Javadoc.
 *
 */
final class OCLintRuleParser implements ServerComponent {

    private static final int OCLINT_MINIMUM_PRIORITY = 3;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(OCLintRuleParser.class);

    public List<Rule> parse(final BufferedReader reader) throws IOException {
        final List<Rule> rules = new ArrayList<Rule>();

        final List<String> listLines = IOUtils.readLines(reader);

        String previousLine = null;
        Rule rule = null;
        boolean inDescription = false;
        for (String line : listLines) {
            if (isLineIgnored(line)) {
                inDescription = false;
            } else if (line.matches("[\\-]{4,}.*")) {
                LOGGER.debug("Rule found : {}", previousLine);

                // remove the rule name from the description of the previous
                // rule
                if (rule != null) {
                    final int index = rule.getDescription().lastIndexOf(
                            previousLine);
                    if (index > 0) {
                        rule.setDescription(rule.getDescription().substring(0,
                                index));
                    }
                }

                rule = Rule.create();
                rules.add(rule);
                rule.setName(previousLine);
                rule.setKey(previousLine);
            } else if (line.matches("Summary:.*")) {
                inDescription = true;
                rule.setDescription(line.substring(line.indexOf(':') + 1));
            } else if (line.matches("Category:.*")) {
                inDescription = true;
            } else if (line.matches("Severity:.*")) {
                inDescription = false;
                final String severity = line.substring("Severity: ".length());
                // Rules are priority 1, 2 or 3 in OCLint files.
                rule.setSeverity(RulePriority.values()[OCLINT_MINIMUM_PRIORITY
                        - Integer.valueOf(severity)]);
            } else {
                if (inDescription) {
                    line = ruleDescriptionLink(line);
                    rule.setDescription(rule.getDescription() + "<br>" + line);
                }
            }
            previousLine = line;
        }
        return rules;
    }

    private boolean isLineIgnored(String line) {
        return line.matches("\\=.*") || line.matches("Priority:.*");
    }

    private String ruleDescriptionLink(final String line) {
        String result = line;
        final int indexOfLink = line.indexOf("http://");
        if (0 <= indexOfLink) {
            final String link = line.substring(indexOfLink);
            final StringBuilder htmlText = new StringBuilder("<a href=\"");
            htmlText.append(link);
            htmlText.append("\" target=\"_blank\">");
            htmlText.append(link);
            htmlText.append("</a>");
            result = htmlText.toString();
        }
        return result;
    }
}
