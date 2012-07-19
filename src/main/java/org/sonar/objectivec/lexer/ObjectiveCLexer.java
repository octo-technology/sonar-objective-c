/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 François Helg, Cyril Picat and OCTO Technology
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
package org.sonar.objectivec.lexer;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCKeyword;
import org.sonar.objectivec.api.ObjectiveCTokenType;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;

public class ObjectiveCLexer {

	private ObjectiveCLexer() {
  }

  public static Lexer create() {
    return create(new ObjectiveCConfiguration());
  }

  public static Lexer create(ObjectiveCConfiguration conf) {
    return Lexer.builder()
        .withCharset(conf.getCharset())

        .withFailIfNoChannelToConsumeOneCharacter(true)

        // Comments
        .withChannel(commentRegexp("//[^\\n\\r]*+"))
        .withChannel(commentRegexp("/\\*[\\s\\S]*?\\*/"))

        // string literals
        .withChannel(regexp(ObjectiveCTokenType.STRING_LITERAL, "\"([^\"\\\\]*+(\\\\[\\s\\S])?+)*+\""))

        // numeric literals
        // integer/long
        // decimal
        .withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "[0-9]++[lL]?+"))
        // hex
        .withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "0[xX][0-9A-Fa-f]++[lL]?+"))
        // float/double
        // decimal
        .withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "[0-9]++[fFdD]"))

        // identifiers and keywords
        // identifiers starts with a non digit and underscore and continues with either one of these or with digits
        // case sensitive = true
        .withChannel(new IdentifierAndKeywordChannel(and("[a-zA-Z_]", o2n("\\w")), true, ObjectiveCKeyword.values()))

        // punctuators/operators
        .withChannel(new PunctuatorChannel(ObjecticeCPunctuator.values()))

        // skip all whitespace chars
        .withChannel(new BlackHoleChannel("[\\s]"))

        .build();
  }
	
}
