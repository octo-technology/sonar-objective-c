/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite,
 *             Denis Bregeon, Mete Balci, Andrés Gil Herrera, Matthew DeTullio
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
package org.sonar.plugins.objectivec.clang;

import org.apache.commons.lang.StringUtils;
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
import org.sonar.api.scan.filesystem.PathResolver;

import java.io.File;
import java.util.List;

/**
 * @author Matthew DeTullio
 */
public final class ClangSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClangSensor.class.getName());

    public static final String REPORTS_PATH_KEY = "sonar.objectivec.clang.reportsPath";

    private final FileSystem fileSystem;
    private final PathResolver pathResolver;
    private final ResourcePerspectives resourcePerspectives;
    private final Settings settings;

    public ClangSensor(final FileSystem fileSystem, final PathResolver pathResolver,
            final ResourcePerspectives resourcePerspectives, final Settings settings) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.resourcePerspectives = resourcePerspectives;
        this.settings = settings;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return StringUtils.isNotEmpty(settings.getString(REPORTS_PATH_KEY));
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        String path = settings.getString(REPORTS_PATH_KEY);
        File reportsDir = pathResolver.relativeFile(fileSystem.baseDir(), path);

        if (!reportsDir.isDirectory()) {
            LOGGER.warn("Clang report directory not found at {}", reportsDir);
            return;
        }

        collect(project, context, reportsDir);
    }

    protected void collect(Project project, SensorContext context, File reportsDir) {
        LOGGER.info("parsing {}", reportsDir);

        List<ClangWarning> clangWarnings = ClangPlistParser.parse(reportsDir);

        for (ClangWarning clangWarning : clangWarnings) {
            String type = clangWarning.getType();
            String ruleKeyName;

            if (ClangRulesDefinition.REPORT_TYPE_TO_RULE_MAP.containsKey(type)) {
                ruleKeyName = ClangRulesDefinition.REPORT_TYPE_TO_RULE_MAP.get(type);
            } else {
                ruleKeyName = "other";
                LOGGER.debug("Type '{}' is not mapped to a rule -- using default rule '{}'", type, ruleKeyName);
            }

            Resource resource = context.getResource(
                    org.sonar.api.resources.File.fromIOFile(clangWarning.getFile(), project));

            if (resource == null) {
                LOGGER.debug("Skipping file (not found in index): {}", clangWarning.getFile().getPath());
                continue;
            }

            Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

            if (issuable != null) {
                Issue issue = issuable.newIssueBuilder()
                        .ruleKey(RuleKey.of(ClangRulesDefinition.REPOSITORY_KEY, ruleKeyName))
                        .message(String.format("%s - %s", clangWarning.getCategory(), type))
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
