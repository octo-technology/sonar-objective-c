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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;
import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCKeyword;
import org.sonar.objectivec.api.ObjectiveCPunctuator;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;
import static org.sonar.objectivec.api.ObjectiveCTokenType.DOUBLE_LITERAL;
import static org.sonar.objectivec.api.ObjectiveCTokenType.FLOAT_LITERAL;
import static org.sonar.objectivec.api.ObjectiveCTokenType.INTEGER_LITERAL;
import static org.sonar.objectivec.api.ObjectiveCTokenType.LONG_LITERAL;

public class ObjectiveCLexer {
    private static final String EXP_REGEXP = "(?:[Ee][+-]?+[0-9_]++)";
    private static final String BINARY_EXP_REGEXP = "(?:[Pp][+-]?+[0-9_]++)";
    private static final String FLOATING_LITERAL_WITHOUT_SUFFIX_REGEXP = "(?:" +
            // Decimal
            "[0-9][0-9_]*+\\.([0-9_]++)?+" + EXP_REGEXP + "?+" +
            "|" + "\\.[0-9][0-9_]*+" + EXP_REGEXP + "?+" +
            "|" + "[0-9][0-9_]*+" + EXP_REGEXP +
            // Hexadecimal
            "|" + "0[xX][0-9_a-fA-F]++\\.[0-9_a-fA-F]*+" + BINARY_EXP_REGEXP +
            "|" + "0[xX][0-9_a-fA-F]++" + BINARY_EXP_REGEXP +
            ")";
    private static final String INTEGER_LITERAL_REGEXP = "(?:" +
            // Hexadecimal
            "0[xX][0-9_a-fA-F]++" +
            // Binary (Java 7)
            "|" + "0[bB][01_]++" +
            // Decimal and Octal
            "|" + "[0-9][0-9_]*+" +
            ")";

    private ObjectiveCLexer() {
        // prevents outside instantiation
    }

    public static Lexer create() {
        return create(new ObjectiveCConfiguration());
    }

    public static Lexer create(ObjectiveCConfiguration conf) {
        return Lexer.builder()
                .withCharset(conf.getCharset())
                .withFailIfNoChannelToConsumeOneCharacter(true)

                /* Remove whitespace */
                .withChannel(new BlackHoleChannel("\\s++"))

                /* Comments */
                .withChannel(commentRegexp("//[^\\n\\r]*+"))
                .withChannel(commentRegexp("/\\*", "[\\s\\S]*?", "\\*/"))

                /* Backslash at the end of the line: just throw away */
                .withChannel(new BackslashChannel())

                /* Character literals */
                .withChannel(new CharacterLiteralsChannel())

                /* String literals */
                .withChannel(new StringLiteralsChannel())

                /* Number literals */
                .withChannel(regexp(FLOAT_LITERAL, FLOATING_LITERAL_WITHOUT_SUFFIX_REGEXP + "[fF]|[0-9][0-9_]*+[fF]"))
                .withChannel(regexp(DOUBLE_LITERAL, FLOATING_LITERAL_WITHOUT_SUFFIX_REGEXP + "[dD]?+|[0-9][0-9_]*+[dD]"))
                .withChannel(regexp(LONG_LITERAL, INTEGER_LITERAL_REGEXP + "[lL]"))
                .withChannel(regexp(INTEGER_LITERAL, INTEGER_LITERAL_REGEXP))

                /* Identifiers, keywords, and punctuators */
                .withChannel(new IdentifierAndKeywordChannel("[#@]?[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?+((\\s+)?\\*)?", true, ObjectiveCKeyword.values()))
                .withChannel(new PunctuatorChannel(ObjectiveCPunctuator.values()))

                /* All other tokens -- must be last channel */
                .withChannel(regexp(GenericTokenType.IDENTIFIER, "[^\r\n\\s/]+"))

                .build();
    }
}
