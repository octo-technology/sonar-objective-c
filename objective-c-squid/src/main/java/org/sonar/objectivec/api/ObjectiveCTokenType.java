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
package org.sonar.objectivec.api;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

import java.util.List;

public enum ObjectiveCTokenType implements TokenType {
    CHARACTER_LITERAL,
    DOUBLE_LITERAL,
    FLOAT_LITERAL,
    INTEGER_LITERAL,
    LONG_LITERAL,
    STRING_LITERAL;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }

    public static List numberLiterals() {
        return ImmutableList.of(DOUBLE_LITERAL, FLOAT_LITERAL, INTEGER_LITERAL, LONG_LITERAL);
    }
}
