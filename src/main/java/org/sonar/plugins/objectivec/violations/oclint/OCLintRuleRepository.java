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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.CharEncoding;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import com.google.common.io.Closeables;

public final class OCLintRuleRepository extends RuleRepository {
    public static final String REPOSITORY_KEY = "OCLint";
    public static final String REPOSITORY_NAME = REPOSITORY_KEY;

    private static final String RULES_FILE = "/org/sonar/plugins/oclint/rules.txt";

    private final OCLintRuleParser ocLintRuleParser = new OCLintRuleParser();

    public OCLintRuleRepository() {
        super(OCLintRuleRepository.REPOSITORY_KEY, ObjectiveC.KEY);
        setName(OCLintRuleRepository.REPOSITORY_NAME);
    }

    @Override
    public List<Rule> createRules() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getClass()
                    .getResourceAsStream(RULES_FILE), CharEncoding.UTF_8));
            return ocLintRuleParser.parse(reader);
        } catch (final IOException e) {
            throw new SonarException("Fail to load the default OCLint rules.",
                    e);
        } finally {
            Closeables.closeQuietly(reader);
        }
    }
}
