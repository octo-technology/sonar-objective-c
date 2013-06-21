/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 François Helg, Cyril Picat and OCTO Technology
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
package org.sonar.objectivec;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;
import org.sonar.objectivec.api.ObjectiveCMetric;
import org.sonar.squid.api.SourceFile;

public class ObjectiveCAstScannerTest {

    @Test
    public void lines() {
        SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
        assertThat(file.getInt(ObjectiveCMetric.LINES), is(18));
    }

    @Test
    public void lines_of_code() {
        SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
        assertThat(file.getInt(ObjectiveCMetric.LINES_OF_CODE), is(5));
    }

    @Test
    public void comments() {
        SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
        assertThat(file.getInt(ObjectiveCMetric.COMMENT_BLANK_LINES), is(3));
        assertThat(file.getInt(ObjectiveCMetric.COMMENT_LINES), is(4));
        assertThat(file.getNoSonarTagLines(), hasItem(10));
        assertThat(file.getNoSonarTagLines().size(), is(1));
    }

}
