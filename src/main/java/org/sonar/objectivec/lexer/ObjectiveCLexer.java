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

import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCKeyword;
import org.sonar.objectivec.api.ObjectiveCPunctuator;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;

import static org.sonar.objectivec.api.ObjectiveCTokenType.NUMERIC_LITERAL;

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

        // All other tokens
        .withChannel(regexp(LITERAL, "[^\\s\\S]+"))

        .withChannel(new BlackHoleChannel("[\\s]"))

        .build();
  }
	
}
