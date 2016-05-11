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
package org.sonar.plugins.objectivec.lizard;

import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.objectivec.api.ObjectiveC;

public final class LizardRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "lizard";
    public static final String REPOSITORY_NAME = "Lizard";

    private static final String THRESHOLD = "Threshold";

    public static final String FILE_CYCLOMATIC_COMPLEXITY_RULE_KEY = "FileCyclomaticComplexity";
    public static final String FILE_CYCLOMATIC_COMPLEXITY_PARAM_KEY = THRESHOLD;

    public static final String FUNCTION_CYCLOMATIC_COMPLEXITY_RULE_KEY = "FunctionCyclomaticComplexity";
    public static final String FUNCTION_CYCLOMATIC_COMPLEXITY_PARAM_KEY = THRESHOLD;

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(REPOSITORY_KEY, ObjectiveC.KEY)
                .setName(REPOSITORY_NAME);

        NewRule fileCyclomaticComplexityRule = repository
                .createRule(FILE_CYCLOMATIC_COMPLEXITY_RULE_KEY)
                .setName("Files should not be too complex")
                .setHtmlDescription("Most of the time, a very complex file breaks the Single Responsibility " +
                        "Principle and should be re-factored into several different files.")
                .setTags("brain-overload")
                .setSeverity(Severity.MAJOR);
        fileCyclomaticComplexityRule
                .setDebtSubCharacteristic(SubCharacteristics.UNIT_TESTABILITY)
                .setDebtRemediationFunction(
                        fileCyclomaticComplexityRule.debtRemediationFunctions().linearWithOffset("1min", "30min"))
                .setEffortToFixDescription("per complexity point above the threshold");
        fileCyclomaticComplexityRule
                .createParam(FILE_CYCLOMATIC_COMPLEXITY_PARAM_KEY)
                .setDefaultValue("80")
                .setType(RuleParamType.INTEGER)
                .setDescription("Maximum complexity allowed.");

        NewRule functionCyclomaticComplexityRule = repository
                .createRule(FUNCTION_CYCLOMATIC_COMPLEXITY_RULE_KEY)
                .setName("Functions should not be too complex")
                .setHtmlDescription("The cyclomatic complexity of functions should not exceed a defined threshold. " +
                        "Complex code can perform poorly and will in any case be difficult to understand and " +
                        "therefore to maintain.")
                .setTags("brain-overload")
                .setSeverity(Severity.MAJOR);
        functionCyclomaticComplexityRule
                .setDebtSubCharacteristic(SubCharacteristics.UNIT_TESTABILITY)
                .setDebtRemediationFunction(
                        functionCyclomaticComplexityRule.debtRemediationFunctions().linearWithOffset("1min", "10min"))
                .setEffortToFixDescription("per complexity point above the threshold");
        functionCyclomaticComplexityRule
                .createParam(FUNCTION_CYCLOMATIC_COMPLEXITY_PARAM_KEY)
                .setDefaultValue("10")
                .setType(RuleParamType.INTEGER)
                .setDescription("Maximum complexity allowed.");

        repository.done();
    }
}
