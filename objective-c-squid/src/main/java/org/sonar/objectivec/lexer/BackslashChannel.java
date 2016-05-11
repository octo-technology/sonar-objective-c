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
package org.sonar.objectivec.lexer;

import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

/**
 * @author Sonar C++ Plugin (Community) authors
 */
public class BackslashChannel extends Channel<Lexer> {
    @Override
    public boolean consume(CodeReader code, Lexer output) {
        char ch = (char) code.peek();

        if ((ch == '\\') && isNewLine(code.charAt(1))) {
            // just throw away the backslash
            code.pop();
            return true;
        }

        return false;
    }

    private static boolean isNewLine(char ch) {
        return (ch == '\n') || (ch == '\r');
    }
}
