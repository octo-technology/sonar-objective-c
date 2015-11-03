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
package org.sonar.plugins.objectivec.oclint;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.plugins.objectivec.ObjectiveC;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

public final class OCLintRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "OCLint";
    public static final String REPOSITORY_NAME = REPOSITORY_KEY;

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(REPOSITORY_KEY, ObjectiveC.KEY)
                .setName(REPOSITORY_NAME);

        RulesDefinitionXmlLoader ruleLoader = new RulesDefinitionXmlLoader();
        ruleLoader.load(
                repository,
                OCLintRulesDefinition.class.getResourceAsStream("/org/sonar/plugins/objectivec/rules-oclint.xml"),
                "UTF-8");

        SqaleXmlLoader.load(repository, "/com/sonar/sqale/oclint-model.xml");

        repository.done();
    }
}
