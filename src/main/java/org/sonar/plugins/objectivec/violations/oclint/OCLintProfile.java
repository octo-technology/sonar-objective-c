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
package org.sonar.plugins.objectivec.violations.oclint;

import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import com.google.common.io.Closeables;

public final class OCLintProfile extends ProfileDefinition {

    private static final String DEFAULT_PROFILE = "/org/sonar/plugins/oclint/profile-oclint.xml";
    private final OCLintProfileImporter profileImporter;

    public OCLintProfile(final OCLintProfileImporter importer) {
        profileImporter = importer;
    }

    @Override
    public RulesProfile createProfile(final ValidationMessages messages) {
        LoggerFactory.getLogger(getClass()).info("Creating OCLint Profile");
        Reader config = null;

        try {
            config = new InputStreamReader(getClass().getResourceAsStream(
                    DEFAULT_PROFILE));
            final RulesProfile profile = profileImporter.importProfile(config,
                    messages);
            profile.setName(OCLintRuleRepository.REPOSITORY_KEY);
            profile.setLanguage(ObjectiveC.KEY);
            profile.setDefaultProfile(true);

            return profile;
        } finally {
            Closeables.closeQuietly(config);
        }
    }
}
