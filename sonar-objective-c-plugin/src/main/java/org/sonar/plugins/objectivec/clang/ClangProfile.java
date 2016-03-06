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
package org.sonar.plugins.objectivec.clang;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.objectivec.ObjectiveC;

import java.io.InputStreamReader;
import java.io.Reader;

public final class ClangProfile extends ProfileDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClangProfile.class);

    private final ClangProfileImporter importer;

    public ClangProfile(final ClangProfileImporter importer) {
        this.importer = importer;
    }

    @Override
    public RulesProfile createProfile(final ValidationMessages messages) {
        LOGGER.info("Creating Clang Profile");
        Reader profileXmlReader = null;

        try {
            profileXmlReader = new InputStreamReader(ClangProfile.class.getResourceAsStream(
                    "/org/sonar/plugins/objectivec/profile-clang.xml"));

            RulesProfile profile = importer.importProfile(profileXmlReader, messages);
            profile.setLanguage(ObjectiveC.KEY);
            profile.setName(ClangRulesDefinition.REPOSITORY_NAME);

            return profile;
        } finally {
            Closeables.closeQuietly(profileXmlReader);
        }
    }
}
