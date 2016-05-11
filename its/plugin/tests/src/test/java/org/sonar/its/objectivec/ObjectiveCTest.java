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
import com.sonar.orchestrator.build.SonarScanner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.fest.assertions.Assertions.assertThat;

public class ObjectiveCTest {
    private static final String PROJECT_KEY = "BitriseSampleUnitAndOtherTestsApp";
    private static final String SRC_DIR_NAME = "BitriseSampleUnitAndOtherTestsApp";

    @ClassRule
    public static Orchestrator orchestrator = Tests.ORCHESTRATOR;

    private static Sonar sonar;

    @BeforeClass
    public static void startServer() throws IOException, URISyntaxException {
        sonar = orchestrator.getServer().getWsClient();
    }

    @Test
    public void testSimpleBuild() {
        SonarScanner scanner = SonarScanner.create()
                .setProjectDir(Tests.projectDirectoryFor(PROJECT_KEY));
        orchestrator.executeBuild(scanner);

        assertThat(getResourceMeasure(PROJECT_KEY, "files").getValue()).isEqualTo(5);
        assertThat(getResourceMeasure(getResourceKey(PROJECT_KEY, "main.m"), "lines").getValue()).isGreaterThan(1);
        assertThat(getResource(getResourceKey(PROJECT_KEY, "DoesNotExist.m"))).isNull();
    }

    private String getResourceKey(String projectKey, String fileName) {
        return projectKey + ":" + SRC_DIR_NAME + "/" + fileName;
    }

    private Resource getResource(String resourceKey) {
        return sonar.find(ResourceQuery.create(resourceKey));
    }

    private Measure getResourceMeasure(String resourceKey, String metricKey) {
        Resource resource = sonar.find(ResourceQuery.createForMetrics(resourceKey, metricKey.trim()));
        assertThat(resource).isNotNull();
        return resource.getMeasure(metricKey.trim());
    }
}
