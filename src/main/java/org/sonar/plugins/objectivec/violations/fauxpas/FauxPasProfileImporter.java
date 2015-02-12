/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology
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
package org.sonar.plugins.objectivec.violations.fauxpas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.io.Reader;

/**
 * Created by gillesgrousset on 12/02/15.
 */
public class FauxPasProfileImporter extends ProfileImporter {

    private static final String UNABLE_TO_LOAD_DEFAULT_PROFILE = "Unable to load default FauxPas profile";
    private static final Logger LOGGER = LoggerFactory.getLogger(FauxPasProfileImporter.class);

    private final XMLProfileParser profileParser;

    public FauxPasProfileImporter(final XMLProfileParser xmlProfileParser) {
        super(FauxPasRuleRepository.REPOSITORY_KEY, FauxPasRuleRepository.REPOSITORY_KEY);
        setSupportedLanguages(ObjectiveC.KEY);
        profileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile importProfile(Reader reader, ValidationMessages messages) {

        final RulesProfile profile = profileParser.parse(reader, messages);

        if (null == profile) {
            messages.addErrorText(UNABLE_TO_LOAD_DEFAULT_PROFILE);
            LOGGER.error(UNABLE_TO_LOAD_DEFAULT_PROFILE);
        }

        return profile;
    }
}
