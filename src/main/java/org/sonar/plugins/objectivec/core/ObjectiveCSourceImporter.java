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
package org.sonar.plugins.objectivec.core;

import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFileUtils;
import org.sonar.api.resources.ProjectFileSystem;

public class ObjectiveCSourceImporter extends AbstractSourceImporter {

    public ObjectiveCSourceImporter(ObjectiveC objectivec) {
        super(objectivec);
    }

    protected void analyse(ProjectFileSystem fileSystem, SensorContext context) {
        parseDirs(context, InputFileUtils.toFiles(fileSystem.mainFiles(ObjectiveC.KEY)), fileSystem.getSourceDirs(), false, fileSystem.getSourceCharset());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
