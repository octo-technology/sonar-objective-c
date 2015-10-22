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
package org.sonar.plugins.objectivec.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.io.File;
import java.util.List;

/**
 * @author Matthew DeTullio
 */
public class ClangSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClangSensor.class.getName());

    private static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX + ".clang.reportsPath";

    private FileSystem fileSystem;
    private ResourcePerspectives resourcePerspectives;
    private RulesProfile rulesProfile;
    private Settings settings;

    public ClangSensor(FileSystem fileSystem, ResourcePerspectives resourcePerspectives, RulesProfile rulesProfile, Settings settings) {
        this.fileSystem = fileSystem;
        this.resourcePerspectives = resourcePerspectives;
        this.rulesProfile = rulesProfile;
        this.settings = settings;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return project.isRoot() && fileSystem.hasFiles(fileSystem.predicates().hasLanguage(ObjectiveC.KEY))
                && !rulesProfile.getActiveRulesByRepository(ClangRulesDefinition.REPOSITORY_KEY).isEmpty();
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        String reportPath = settings.getString(REPORT_PATH_KEY);
        if (reportPath == null) {
            return;
        }

        LOGGER.info("Processing Clang reports path: {}", reportPath);

        List<ClangWarning> clangWarnings = ClangPlistParser.parse(new File(reportPath));

        for (ClangWarning clangWarning : clangWarnings) {
            // TODO: Add check for enabled rule if/when rules get split up

            Resource resource = context.getResource(
                    org.sonar.api.resources.File.fromIOFile(clangWarning.getFile(), project));

            if (resource == null) {
                LOGGER.debug("Skipping file (not found in index): {}", clangWarning.getFile().getPath());
                continue;
            }

            Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

            if (issuable != null) {
                Issue issue = issuable.newIssueBuilder()
                        .ruleKey(RuleKey.of(ClangRulesDefinition.REPOSITORY_KEY, "other"))
                        .message(String.format("%s - %s", clangWarning.getCategory(), clangWarning.getType()))
                        .line(clangWarning.getLine())
                        .build();

                issuable.addIssue(issue);
            }
        }
    }

    @Override
    public String toString() {
        return "Objective-C Clang Sensor";
    }
}
