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
package org.sonar.objectivec.parser;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;

import org.sonar.objectivec.api.ObjectiveCGrammar;

import static org.sonar.objectivec.api.ObjectiveCPunctuator.LBRACKET;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.RBRACKET;

import static org.sonar.objectivec.api.ObjectiveCTokenType.STRING_LITERAL;
import static org.sonar.objectivec.api.ObjectiveCTokenType.NUMERIC_LITERAL;

public class ObjectiveCGrammarImpl extends ObjectiveCGrammar {

	public ObjectiveCGrammarImpl() {

        messageReceiver.is(IDENTIFIER);
        messageSent.is(IDENTIFIER);

        sendMessageExpression.is(and(LBRACKET, messageReceiver, messageSent, RBRACKET));

        statement.is(or(sendMessageExpression));

        program.is(statement);

  }
	
}
