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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.objectivec.ObjectiveC;

import java.io.Reader;

public final class ClangProfileImporter extends ProfileImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClangProfileImporter.class);
    private static final String UNABLE_TO_LOAD_DEFAULT_PROFILE = "Unable to load default Clang profile";

    private final XMLProfileParser profileParser;

    public ClangProfileImporter(final XMLProfileParser xmlProfileParser) {
        super(ClangRulesDefinition.REPOSITORY_KEY, ClangRulesDefinition.REPOSITORY_NAME);
        setSupportedLanguages(ObjectiveC.KEY);
        profileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile importProfile(final Reader reader,
            final ValidationMessages messages) {
        final RulesProfile profile = profileParser.parse(reader, messages);

        if (null == profile) {
            messages.addErrorText(UNABLE_TO_LOAD_DEFAULT_PROFILE);
            LOGGER.error(UNABLE_TO_LOAD_DEFAULT_PROFILE);
        }

        return profile;
    }
}
