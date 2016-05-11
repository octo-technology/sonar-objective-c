/*
 * SonarQube Objective-C (Community) :: Squid
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
package org.sonar.objectivec;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.objectivec.api.ObjectiveCMetric;
import org.sonar.objectivec.highlighter.SonarComponents;
import org.sonar.objectivec.highlighter.SyntaxHighlighterVisitor;
import org.sonar.objectivec.parser.ObjectiveCParser;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.CommentAnalyser;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.metrics.CommentsVisitor;
import org.sonar.squidbridge.metrics.LinesOfCodeVisitor;
import org.sonar.squidbridge.metrics.LinesVisitor;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;

public class ObjectiveCAstScanner {

    private ObjectiveCAstScanner() {
        // prevents outside instantiation
    }

    /**
     * Helper method for testing checks without having to deploy them on a Sonar instance.
     */
    @SafeVarargs
    public static SourceFile scanSingleFile(File file, SquidAstVisitor<Grammar>... visitors) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("File '" + file + "' not found.");
        }
        AstScanner<Grammar> scanner = create(new ObjectiveCConfiguration(), null, visitors);
        scanner.scanFile(file);
        Collection<SourceCode> sources = scanner.getIndex().search(new QueryByType(SourceFile.class));
        if (sources.size() != 1) {
            throw new IllegalStateException("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
        }
        return (SourceFile) sources.iterator().next();
    }

    @SafeVarargs
    public static AstScanner<Grammar> create(ObjectiveCConfiguration conf,
            @Nullable SonarComponents sonarComponents, SquidAstVisitor<Grammar>... visitors) {
        final SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<>(new SourceProject("Objective-C Project"));
        final Parser<Grammar> parser = ObjectiveCParser.create(conf);

        AstScanner.Builder<Grammar> builder = AstScanner.builder(context).setBaseParser(parser);

        /* Metrics */
        builder.withMetrics(ObjectiveCMetric.values());

        /* Comments */
        builder.setCommentAnalyser(
                new CommentAnalyser() {
                    @Override
                    public boolean isBlank(String line) {
                        for (int i = 0; i < line.length(); i++) {
                            if (Character.isLetterOrDigit(line.charAt(i))) {
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    public String getContents(String comment) {
                        return comment.startsWith("//") ? comment.substring(2) : comment.substring(2, comment.length() - 2);
                    }
                });

        /* Files */
        builder.setFilesMetric(ObjectiveCMetric.FILES);

        /* Metrics */
        builder.withSquidAstVisitor(new LinesVisitor<>(ObjectiveCMetric.LINES));
        builder.withSquidAstVisitor(new LinesOfCodeVisitor<>(ObjectiveCMetric.LINES_OF_CODE));
        builder.withSquidAstVisitor(CommentsVisitor.builder().withCommentMetric(ObjectiveCMetric.COMMENT_LINES)
                .withNoSonar(true)
                .withIgnoreHeaderComment(conf.getIgnoreHeaderComments())
                .build());

        /* Syntax highlighter */
        if (sonarComponents != null) {
            builder.withSquidAstVisitor(new SyntaxHighlighterVisitor(sonarComponents, conf.getCharset()));
        }

        /* External visitors */
        for (SquidAstVisitor<Grammar> visitor : visitors) {
            builder.withSquidAstVisitor(visitor);
        }

        return builder.build();
    }

}
