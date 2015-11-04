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
package org.sonar.objectivec.parser;

import com.sonar.sslr.impl.Parser;
import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCGrammar;
import org.sonar.objectivec.lexer.ObjectiveCLexer;

public class ObjectiveCParser {

    private ObjectiveCParser() {
        // Prevent outside instantiation
    }

    public static Parser<ObjectiveCGrammar> create() {
        return create(new ObjectiveCConfiguration());
    }

    public static Parser<ObjectiveCGrammar> create(ObjectiveCConfiguration conf) {
        return Parser.builder((ObjectiveCGrammar) new ObjectiveCGrammarImpl())
                .withLexer(ObjectiveCLexer.create(conf))
                .build();
    }

}
