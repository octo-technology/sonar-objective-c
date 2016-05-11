/*
 * SonarQube Objective-C (Community) :: ITs :: Plugin :: Tests
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
package org.sonar.its.objectivec;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ObjectiveCTest.class
})
public class Tests {

    public static final String PROJECT_ROOT_DIR = "../projects/";
    private static final String PLUGIN_KEY = "objectivec";

    @ClassRule
    public static final Orchestrator ORCHESTRATOR;

    static {
        OrchestratorBuilder orchestratorBuilder = Orchestrator.builderEnv()
                .addPlugin(FileLocation.byWildcardMavenFilename(
                        new File("../../../sonar-objective-c-plugin/target"), "sonar-objective-c-plugin-*.jar"));
        ORCHESTRATOR = orchestratorBuilder.build();
    }

    public static boolean is_after_plugin(String version) {
        return ORCHESTRATOR.getConfiguration().getPluginVersion(PLUGIN_KEY).isGreaterThanOrEquals(version);
    }

    public static File projectDirectoryFor(String projectDirName) {
        return new File(Tests.PROJECT_ROOT_DIR + projectDirName + "/");
    }
}