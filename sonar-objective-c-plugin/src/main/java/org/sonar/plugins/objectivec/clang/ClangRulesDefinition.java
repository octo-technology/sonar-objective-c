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
package org.sonar.plugins.objectivec.clang;

import com.google.common.collect.ImmutableMap;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.plugins.objectivec.api.ObjectiveC;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.util.Map;

/**
 * @author Matthew DeTullio
 */
public final class ClangRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "clang";
    public static final String REPOSITORY_NAME = "Clang";

    /**
     * Map of Clang plist report <code>type</code>s to their corresponding rules.  Multiple types can map to a single
     * rule.
     */
    public static final Map<String, String> REPORT_TYPE_TO_RULE_MAP = ImmutableMap.<String, String>builder()
            .put("Assigned value is garbage or undefined", "core.uninitialized.Assign") // Logic error
            .put("Bad return type when passing NSError**", "osx.cocoa.NSError") // Coding conventions (Apple)
            .put("Branch condition evaluates to a garbage value", "core.uninitialized.Branch") // Logic error
            .put("Dead assignment", "deadcode.DeadStores") // Dead store
            .put("Dead increment", "deadcode.DeadStores") // Dead store
            .put("Dead initialization", "deadcode.DeadStores") // Dead store
            .put("Garbage return value", "core.uninitialized.UndefReturn") // Logic error
            .put("Leak", "osx.cocoa.RetainCount") // Memory (Core Foundation/Objective-C)
            .put("Missing call to superclass", "osx.cocoa.MissingSuperCall") // Core Foundation/Objective-C
            .put("Nil value used as mutex for @synchronized() (no synchronization will occur)", "osx.cocoa.AtSync") // Logic error
            .put("Result of operation is garbage or undefined", "core.UndefinedBinaryOperatorResult") // Logic error
            .put("Uninitialized argument value", "core.CallAndMessage") // Logic error
            .build();

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(REPOSITORY_KEY, ObjectiveC.KEY)
                .setName(REPOSITORY_NAME);

        RulesDefinitionXmlLoader ruleLoader = new RulesDefinitionXmlLoader();
        ruleLoader.load(
                repository,
                ClangRulesDefinition.class.getResourceAsStream("/org/sonar/plugins/objectivec/rules-clang.xml"),
                "UTF-8");

        SqaleXmlLoader.load(repository, "/com/sonar/sqale/clang-model.xml");

        repository.done();
    }
}
