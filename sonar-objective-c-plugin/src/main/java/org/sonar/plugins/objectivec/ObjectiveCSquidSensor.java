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
package org.sonar.plugins.objectivec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.objectivec.ObjectiveCAstScanner;
import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCMetric;
import org.sonar.objectivec.checks.CheckList;
import org.sonar.objectivec.highlighter.SonarComponents;
import org.sonar.plugins.objectivec.api.ObjectiveC;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.squidbridge.indexer.QueryByType;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


public class ObjectiveCSquidSensor implements Sensor {
    private SensorContext context;

    private final Checks<SquidCheck<Grammar>> checks;
    private final FileSystem fileSystem;
    private final FilePredicate mainFilePredicates;
    private final PathResolver pathResolver;
    private final ResourcePerspectives resourcePerspectives;

    public ObjectiveCSquidSensor(CheckFactory checkFactory, FileSystem fileSystem,
            ResourcePerspectives resourcePerspectives, PathResolver pathResolver) {
        this.checks = checkFactory
                .<SquidCheck<Grammar>>create(CheckList.REPOSITORY_KEY)
                .addAnnotatedChecks(CheckList.getChecks());
        this.fileSystem = fileSystem;
        this.mainFilePredicates = fileSystem.predicates().and(
                fileSystem.predicates().hasLanguage(ObjectiveC.KEY),
                fileSystem.predicates().hasType(InputFile.Type.MAIN));
        this.pathResolver = pathResolver;
        this.resourcePerspectives = resourcePerspectives;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return project.isRoot() && fileSystem.hasFiles(fileSystem.predicates().hasLanguage(ObjectiveC.KEY));
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        this.context = context;

        List<SquidAstVisitor<Grammar>> visitors = Lists.<SquidAstVisitor<Grammar>>newArrayList(checks.all());

        @SuppressWarnings("unchecked") AstScanner<Grammar> scanner = ObjectiveCAstScanner.create(
                createConfiguration(), new SonarComponents(resourcePerspectives, fileSystem),
                visitors.toArray(new SquidAstVisitor[visitors.size()]));

        scanner.scanFiles(ImmutableList.copyOf(fileSystem.files(mainFilePredicates)));

        Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
        save(squidSourceFiles);
    }

    private ObjectiveCConfiguration createConfiguration() {
        return new ObjectiveCConfiguration(fileSystem.encoding());
    }

    private void save(Collection<SourceCode> squidSourceFiles) {
        for (SourceCode squidSourceFile : squidSourceFiles) {
            SourceFile squidFile = (SourceFile) squidSourceFile;

            String relativePath = pathResolver.relativePath(fileSystem.baseDir(), new File(squidFile.getKey()));
            InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasRelativePath(relativePath));

            /*
             * Distribution is saved in the Lizard sensor and therefore it is not possible to save the complexity
             * distribution here. The functionality has been moved to LizardParser.
            */
            //saveFilesComplexityDistribution(sonarFile, squidFile);
            //saveFunctionsComplexityDistribution(sonarFile, squidFile);
            saveMeasures(inputFile, squidFile);
            saveViolations(inputFile, squidFile);
        }
    }

    private void saveMeasures(InputFile inputFile, SourceFile squidFile) {
        context.saveMeasure(inputFile, CoreMetrics.FILES, squidFile.getDouble(ObjectiveCMetric.FILES));
        context.saveMeasure(inputFile, CoreMetrics.LINES, squidFile.getDouble(ObjectiveCMetric.LINES));
        context.saveMeasure(inputFile, CoreMetrics.NCLOC, squidFile.getDouble(ObjectiveCMetric.LINES_OF_CODE));
        context.saveMeasure(inputFile, CoreMetrics.COMMENT_LINES, squidFile.getDouble(ObjectiveCMetric.COMMENT_LINES));
        /*
         * Not implemented
         */
        //context.saveMeasure(inputFile, CoreMetrics.CLASSES, squidFile.getDouble(ObjectiveCMetric.CLASSES));
        //context.saveMeasure(inputFile, CoreMetrics.STATEMENTS, squidFile.getDouble(ObjectiveCMetric.STATEMENTS));
        /*
         * Saving the same measure more than once per file throws  exception. That is why
         * CoreMetrics.FUNCTIONS and CoreMetrics.COMPLEXITY are not allowed to be saved here. In order for the
         * LizardSensor to be able to to its job and save the values for those metrics the functionality has been
         * moved to Lizard classes.
         */
        //context.saveMeasure(inputFile, CoreMetrics.FUNCTIONS, squidFile.getDouble(ObjectiveCMetric.FUNCTIONS));
        //context.saveMeasure(inputFile, CoreMetrics.COMPLEXITY, squidFile.getDouble(ObjectiveCMetric.COMPLEXITY));
    }

    private void saveViolations(@Nullable InputFile inputFile, SourceFile squidFile) {
        Collection<CheckMessage> messages = squidFile.getCheckMessages();

        final Resource resource = inputFile == null ? null : context.getResource(inputFile);

        if (messages != null && resource != null) {
            for (CheckMessage message : messages) {
                @SuppressWarnings("unchecked") RuleKey ruleKey =
                        checks.ruleKey((SquidCheck<Grammar>) message.getCheck());

                Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

                if (issuable != null) {
                    IssueBuilder issueBuilder = issuable.newIssueBuilder()
                            .ruleKey(ruleKey)
                            .line(message.getLine())
                            .message(message.getText(Locale.ENGLISH));

                    if (message.getCost() != null) {
                        issueBuilder.effortToFix(message.getCost());
                    }

                    issuable.addIssue(issueBuilder.build());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Objective-C Squid Sensor";
    }

}
