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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import java.util.List;

public class ObjectiveC extends AbstractLanguage {

    /**
     * Objective-C key
     */
    public static final String KEY = "objectivec";

    /**
     * Objective-C name
     */
    public static final String NAME = "Objective-C (Community)";

    /**
     * Key of the file suffix parameter
     */
    public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";

    /**
     * Default Java files knows suffixes
     */
    public static final String DEFAULT_FILE_SUFFIXES = ".h,.m";

    private Settings settings;

    /**
     * Default constructor
     *
     * @param settings Injected project batch and global server settings
     */
    public ObjectiveC(Settings settings) {
        super(KEY, NAME);
        this.settings = settings;
    }

    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(settings.getStringArray(ObjectiveC.FILE_SUFFIXES_KEY));
        if (suffixes.length == 0) {
            suffixes = StringUtils.split(ObjectiveC.DEFAULT_FILE_SUFFIXES, ",");
        }
        return suffixes;
    }

    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = Lists.newArrayList();
        for (String string : stringArray) {
            if (StringUtils.isNotBlank(string.trim())) {
                nonEmptyStrings.add(string.trim());
            }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
    }
}
