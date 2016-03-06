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
package org.sonar.objectivec.highlighter;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.source.Highlightable;
import org.sonar.objectivec.api.ObjectiveCKeyword;
import org.sonar.objectivec.api.ObjectiveCTokenType;
import org.sonar.squidbridge.SquidAstVisitor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class SyntaxHighlighterVisitor extends SquidAstVisitor<Grammar> implements AstAndTokenVisitor {
    private static final Map<AstNodeType, String> TYPES;

    static {
        ImmutableMap.Builder<AstNodeType, String> typesBuilder = ImmutableMap.builder();
        // Add grammar types to highlight here
        TYPES = typesBuilder.build();
    }

    private final SonarComponents sonarComponents;
    private final Charset charset;

    private Highlightable.HighlightingBuilder highlighting;
    private List<Integer> lineStart;

    public SyntaxHighlighterVisitor(SonarComponents sonarComponents, Charset charset) {
        this.sonarComponents = Preconditions.checkNotNull(sonarComponents);
        this.charset = charset;
    }

    @Override
    public void init() {
        for (AstNodeType type : TYPES.keySet()) {
            subscribeTo(type);
        }
    }

    @Override
    public void visitFile(AstNode astNode) {
        if (astNode == null) {
            // parse error
            return;
        }

        InputFile inputFile = sonarComponents.inputFileFor(getContext().getFile());
        Preconditions.checkNotNull(inputFile);
        highlighting = sonarComponents.highlightableFor(inputFile).newHighlighting();

        lineStart = Lists.newArrayList();
        final String content;
        try {
            content = Files.toString(getContext().getFile(), charset);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        lineStart.add(0);
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n' || (content.charAt(i) == '\r' && i + 1 < content.length() && content.charAt(i + 1) != '\n')) {
                lineStart.add(i + 1);
            }
        }
    }

    @Override
    public void visitNode(AstNode astNode) {
        highlighting.highlight(astNode.getFromIndex(), astNode.getToIndex(), TYPES.get(astNode.getType()));
    }

    @Override
    public void visitToken(Token token) {
        // Use org.sonar.api.batch.sensor.highlighting.TypeOfText here?

        for (Trivia trivia : token.getTrivia()) {
            if (trivia.isComment()) {
                Token triviaToken = trivia.getToken();
                if (triviaToken.getValue().startsWith("/**")) {
                    highlightToken(triviaToken, "j");
                } else if (triviaToken.getValue().startsWith("/*")) {
                    highlightToken(triviaToken, "cppd");
                } else {
                    highlightToken(triviaToken, "cd");
                }
            }
        }

        if (token.getType() instanceof ObjectiveCKeyword) {
            highlightToken(token, "k");
        }

        if (ObjectiveCTokenType.numberLiterals().contains(token.getType())) {
            highlightToken(token, "c");
        }

        if (ObjectiveCTokenType.STRING_LITERAL.equals(token.getType())
                || ObjectiveCTokenType.CHARACTER_LITERAL.equals(token.getType())) {
            highlightToken(token, "s");
        }
    }

    private void highlightToken(Token token, String typeOfText) {
        int offset = getOffset(token.getLine(), token.getColumn());
        highlighting.highlight(offset, offset + token.getValue().length(), typeOfText);
    }

    /**
     * @param line   starts from 1
     * @param column starts from 0
     */
    private int getOffset(int line, int column) {
        return lineStart.get(line - 1) + column;
    }

    @Override
    public void leaveFile(AstNode astNode) {
        if (astNode == null) {
            // parse error
            return;
        }
        highlighting.done();
    }
}
