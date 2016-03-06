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
package org.sonar.objectivec.lexer;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.objectivec.api.ObjectiveCKeyword;

public class ObjectiveCLexerTest {

    private static Lexer lexer;

    @BeforeClass
    public static void init() {
        lexer = ObjectiveCLexer.create();
    }

    @Test
    public void lexMultiLinesComment() {
        assertThat(lexer.lex("/* My Comment \n*/"), hasComment("/* My Comment \n*/"));
        assertThat(lexer.lex("/**/"), hasComment("/**/"));
    }

    @Test
    public void lexInlineComment() {
        assertThat(lexer.lex("// My Comment \n new line"), hasComment("// My Comment "));
        assertThat(lexer.lex("//"), hasComment("//"));
    }

    @Test
    public void lexEndOflineComment() {
        assertThat(lexer.lex("[self init]; // My Comment end of line"), hasComment("// My Comment end of line"));
        assertThat(lexer.lex("[self init]; //"), hasComment("//"));
    }

    @Test
    public void lexLineOfCode() {
        assertThat(lexer.lex("[self init];"), hasToken("self", ObjectiveCKeyword.SELF));
    }

    @Test
    public void lexEmptyLine() {
        List<Token> tokens = lexer.lex("\n");
        assertThat(tokens.size(), equalTo(1));
        assertThat(tokens, hasToken(GenericTokenType.EOF));
    }

    @Test
    public void lexSampleFile() {
        List<Token> tokens = lexer.lex(new File("src/test/resources/objcSample.h"));
        assertThat(tokens.size(), equalTo(24));
        assertThat(tokens, hasToken(GenericTokenType.EOF));
    }

}
