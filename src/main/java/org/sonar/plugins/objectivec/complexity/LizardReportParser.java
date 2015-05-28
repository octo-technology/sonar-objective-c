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
package org.sonar.plugins.objectivec.complexity;

import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by agilherr on 28/05/15.
 */
public class LizardReportParser {

    private static final String MEASURE = "measure";
    private static final String MEASURE_TYPE = "type";
    private static final String MEASURE_ITEM = "item";
    private static final String FILE_MEASURE = "file";
    private static final String FUNCTION_MEASURE = "Function";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final int CYCLOMATIC_COMPLEXITY_INDEX = 2;
    private static final int FUNCTIONS_INDEX = 3;

    public Map<String, List<Measure>> parseReport(final File xmlFile) {
        Map<String, List<Measure>> result = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            result = parseFile(document);
        } catch (final IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error processing file named {}", xmlFile, e);
        } catch (ParserConfigurationException e) {
            LoggerFactory.getLogger(getClass()).error("Error processing file named {}", xmlFile, e);
        } catch (SAXException e) {
            LoggerFactory.getLogger(getClass()).error("Error processing file named {}", xmlFile, e);
        }

        return result;
    }

    public Map<String, List<Measure>> parseFile(Document document) {
        final Map<String, List<Measure>> reportMeasures = new HashMap<String, List<Measure>>();
        final List<ObjCFunction> functions = new ArrayList<ObjCFunction>();

        NodeList nodeList = document.getElementsByTagName(MEASURE);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute(MEASURE_TYPE).equalsIgnoreCase(FILE_MEASURE)) {
                    NodeList itemList = element.getElementsByTagName(MEASURE_ITEM);
                    addComplexityPerFileMeasure(itemList, reportMeasures);
                } else if(element.getAttribute(MEASURE_TYPE).equalsIgnoreCase(FUNCTION_MEASURE)) {
                    NodeList itemList = element.getElementsByTagName(MEASURE_ITEM);
                    collectFunctions(itemList, functions);
                }
            }
        }

        addComplexityPerFunctionMeasure(reportMeasures, functions);

        return reportMeasures;
    }

    private void addComplexityPerFileMeasure(NodeList itemList, Map<String, List<Measure>> reportMeasures){
        for (int i = 0; i < itemList.getLength(); i++) {
            Node item = itemList.item(i);

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) item;
                String fileName = itemElement.getAttribute(NAME);
                NodeList values = itemElement.getElementsByTagName(VALUE);
                int complexity = Integer.parseInt(values.item(CYCLOMATIC_COMPLEXITY_INDEX).getTextContent());
                double fileComplexity = Double.parseDouble(values.item(CYCLOMATIC_COMPLEXITY_INDEX).getTextContent());
                int numberOfFunctions =  Integer.parseInt(values.item(FUNCTIONS_INDEX).getTextContent());

                List<Measure> list = new ArrayList<Measure>();
                Metric complexityMetric = CoreMetrics.COMPLEXITY;
                complexityMetric.setDomain("complexity");
                list.add(new Measure(CoreMetrics.COMPLEXITY).setIntValue(complexity));
                list.add(new Measure(CoreMetrics.FUNCTIONS).setIntValue(numberOfFunctions));//TODO throws exception while saving
                list.add(new Measure(CoreMetrics.FILE_COMPLEXITY, fileComplexity));

                reportMeasures.put(fileName, list);
            }
        }
    }

    private void collectFunctions(NodeList itemList, List<ObjCFunction> functions) {
        for (int i = 0; i < itemList.getLength(); i++) {
            Node item = itemList.item(i);

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) item;
                String name = itemElement.getAttribute(NAME);
                String measure = itemElement.getElementsByTagName(VALUE).item(CYCLOMATIC_COMPLEXITY_INDEX).getTextContent();
                functions.add(new ObjCFunction(name, Integer.parseInt(measure)));
            }
        }
    }

    private void addComplexityPerFunctionMeasure(Map<String, List<Measure>> reportMeasures, List<ObjCFunction> functions){
        for (Map.Entry<String, List<Measure>> entry : reportMeasures.entrySet()) {
            int count = 0;
            int complexityInFunctions = 0;
            for (ObjCFunction func : functions) {
                if (func.getName().contains(entry.getKey())) {
                    count++;
                    complexityInFunctions += func.getCyclomaticComplexity();
                }
            }

            int complex = 0;
            for (Measure m : entry.getValue()){
                if (m.getMetric().getKey().equalsIgnoreCase(CoreMetrics.FILE_COMPLEXITY.getKey())){
                    complex = m.getIntValue();
                    break;
                }
            }

            double complexMean = 0;
            if (count != 0) {
                complexMean = (double)complex/(double)count;
            }

            entry.getValue().add(new Measure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS).setIntValue(complexityInFunctions));
            entry.getValue().add(new Measure(CoreMetrics.FUNCTION_COMPLEXITY, complexMean));
        }
    }

    private class ObjCFunction {
        private String name;
        private int cyclomaticComplexity;

        public ObjCFunction(String name, int cyclomaticComplexity) {
            this.name = name;
            this.cyclomaticComplexity = cyclomaticComplexity;
        }

        public String getName() {
            return name;
        }

        public int getCyclomaticComplexity() {
            return cyclomaticComplexity;
        }

    }
}
