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
package org.sonar.plugins.objectivec.violations.fauxpas;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FauxPasRuleParser implements ServerComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(FauxPasRuleParser.class);

    public List<Rule> parse(final BufferedReader reader) throws IOException {
        final List<Rule> rules = new ArrayList<Rule>();

        String jsonString = IOUtils.toString(reader);

        Object rulesObj = JSONValue.parse(jsonString);

        if (rulesObj != null) {
            JSONArray fpRules = (JSONArray)rulesObj;
            for (Object obj : fpRules) {
                JSONObject fpRule = (JSONObject)obj;

                Rule rule = Rule.create();
                rules.add(rule);
                rule.setName((String) fpRule.get("name"));
                rule.setKey((String) fpRule.get("key"));
                rule.setDescription((String) fpRule.get("description"));
                rule.setSeverity(RulePriority.valueOf((String) fpRule.get("severity")));
            }
        }


        return rules;
    }
}
