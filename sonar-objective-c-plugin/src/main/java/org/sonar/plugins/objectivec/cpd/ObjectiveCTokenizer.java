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
package org.sonar.plugins.objectivec.cpd;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.lexer.ObjectiveCLexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ObjectiveCTokenizer implements Tokenizer {

    private final Charset charset;

    public ObjectiveCTokenizer(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void tokenize(SourceCode source, Tokens cpdTokens) throws IOException {
        Lexer lexer = ObjectiveCLexer.create(new ObjectiveCConfiguration(charset));
        String fileName = source.getFileName();
        List<Token> tokens = lexer.lex(new File(fileName));
        for (Token token : tokens) {
            TokenEntry cpdToken = new TokenEntry(getTokenImage(token), fileName, token.getLine());
            cpdTokens.add(cpdToken);
        }
        cpdTokens.add(TokenEntry.getEOF());
    }

    private String getTokenImage(Token token) {
        return token.getValue();
    }

}
