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
package org.sonar.plugins.objectivec;

import com.google.common.collect.ImmutableList;
import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.CDocTokenizer;
import org.sonar.colorizer.CppDocTokenizer;
import org.sonar.colorizer.JavadocTokenizer;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.StringTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.objectivec.api.ObjectiveCKeyword;

import java.util.List;

public class ObjectiveCColorizerFormat extends CodeColorizerFormat {

    public ObjectiveCColorizerFormat() {
        super(ObjectiveC.KEY);
    }

    @Override
    public List<Tokenizer> getTokenizers() {
        return ImmutableList.of(
                new StringTokenizer("<span class=\"s\">", "</span>"),
                new CDocTokenizer("<span class=\"cd\">", "</span>"),
                new JavadocTokenizer("<span class=\"cppd\">", "</span>"),
                new CppDocTokenizer("<span class=\"cppd\">", "</span>"),
                new KeywordsTokenizer("<span class=\"k\">", "</span>", ObjectiveCKeyword.keywordValues()));
    }

}
