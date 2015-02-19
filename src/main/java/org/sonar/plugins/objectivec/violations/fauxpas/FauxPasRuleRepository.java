/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012-2015 OCTO Technology, Backelite
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

import com.google.common.io.Closeables;
import org.apache.commons.lang.CharEncoding;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class FauxPasRuleRepository extends RuleRepository {

    public static final String REPOSITORY_KEY = "FauxPas";
    public static final String REPOSITORY_NAME = REPOSITORY_KEY;

    private static final String RULES_FILE = "/org/sonar/plugins/fauxpas/rules.json";

    private final FauxPasRuleParser ruleParser = new FauxPasRuleParser();

    public FauxPasRuleRepository() {
        super(FauxPasRuleRepository.REPOSITORY_KEY, ObjectiveC.KEY);
        setName(FauxPasRuleRepository.REPOSITORY_NAME);
    }

    @Override
    public List<Rule> createRules() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getClass()
                    .getResourceAsStream(RULES_FILE), CharEncoding.UTF_8));
            return ruleParser.parse(reader);
        } catch (final IOException e) {
            throw new SonarException("Fail to load the default FauxPas rules.",
                    e);
        } finally {
            Closeables.closeQuietly(reader);
        }
    }
}
