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
package org.sonar.objectivec.api;

import org.sonar.squidbridge.measures.CalculatedMetricFormula;
import org.sonar.squidbridge.measures.MetricDef;

public enum ObjectiveCMetric implements MetricDef {
    FILES,
    LINES,
    LINES_OF_CODE,
    COMMENT_LINES,
    COMPLEXITY,
    FUNCTIONS;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isCalculatedMetric() {
        return false;
    }

    @Override
    public boolean aggregateIfThereIsAlreadyAValue() {
        return true;
    }

    @Override
    public boolean isThereAggregationFormula() {
        return true;
    }

    @Override
    public CalculatedMetricFormula getCalculatedMetricFormula() {
        return null;
    }
}
