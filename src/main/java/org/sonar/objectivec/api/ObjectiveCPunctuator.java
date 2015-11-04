/*
 * SonarQube Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite, SonarSource,
 *             Denis Bregeon, Mete Balci, Andrés Gil Herrera, Matthew DeTullio
 * sonarqube@googlegroups.com
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
package org.sonar.objectivec.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public enum ObjectiveCPunctuator implements TokenType {

    // these are really only c operators
    // http://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B

    PLUSPLUS("++"),
    PLUSEQ("+="),
    PLUS("+"),

    MINUSMINUS("--"),
    MINUSEQ("-="),
    MINUS("-"),

    STAREQ("*="),
    STAR("*"),

    SLASHEQ("/="),
    SLASH("/"),

    LT("<"),
    LTLT("<<"),
    LTEQ("<="),
    LTLTEQ("<<="),

    GT(">"),
    GTGT(">>"),
    GTEQ(">="),
    GTGTEQ(">>="),

    EQ("="),
    EQEQ("=="),

    TILDE("~"),

    EXCL("!"),
    EXCLEQ("!="),

    AMP("&"),
    AMPAMP("&&"),
    AMPEQ("&="),
    AMPAMPEX("&&="),

    BAR("|"),
    BARBAR("||"),
    BAREQ("|="),
    BARBAREQ("||="),

    CARETEQ("^="),
    CARET("^"),

    PERCENT("%"),
    PERCENTEQ("%="),

    LCURLYBRACE("{"),
    RCURLYBRACE("}"),
    LPARENTHESIS("("),
    RPARENTHESIS(")"),
    LBRACKET("["),
    RBRACKET("]"),

    QUESTION("?"),
    COLON(":"),
    SEMICOLON(";"),
    COMMA(","),

    MINUSLT("->"),
    MINUSLTSTAR("->*"),
    DOTSTAR(".*");

    private final String value;

    private ObjectiveCPunctuator(String word) {
        this.value = word;
    }

    public String getName() {
        return name();
    }

    public String getValue() {
        return value;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }

}
