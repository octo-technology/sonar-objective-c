/*
 * SonarQube Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology, Backelite, SonarSource,
 *             Denis Bregeon, Mete Balci, Andrés Gil Herrera, Matthew DeTullio
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.objectivec.clang;

import com.dd.plist.PropertyListFormatException;
import com.dd.plist.XMLPropertyListParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.XmlParserException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew DeTullio
 */
public final class ClangPlistParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClangPlistParser.class);

    public static List<ClangWarning> parse(final File reportsDir) {
        List<ClangWarning> result = new ArrayList<ClangWarning>();

        File[] reports = getReports(reportsDir);

        for (File report : reports) {
            try {
                result.addAll(parsePlist(report));
            } catch (Exception e) {
                throw new XmlParserException("Unable to parse Clang reports", e);
            }
        }

        return result;
    }

    private static File[] getReports(final File reportsDir) {
        if (!reportsDir.isDirectory() || !reportsDir.exists()) {
            return new File[0];
        }

        return reportsDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".plist");
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static List<ClangWarning> parsePlist(final File file) {
        List<ClangWarning> result = new ArrayList<ClangWarning>();

        try {
            // Clang report is NSDictionary, which converts to a Map
            Map<String, Object> report =
                    (Map<String, Object>) XMLPropertyListParser.parse(file).toJavaObject();

            // Files reported on in this report
            List<String> files = new ArrayList<String>();
            for (Object obj : (Object[]) report.get("files")) {
                files.add((String) obj);
            }

            // Diagnostics which contain the warning and the execution path
            // (we're only interested in the final location)
            List<Map<String, Object>> diagnostics = new ArrayList<Map<String, Object>>();
            for (Object obj : (Object[]) report.get("diagnostics")) {
                diagnostics.add((Map<String, Object>) obj);
            }

            // Extract warning type, line number, and file, then add to results
            for (Map<String, Object> diagnostic : diagnostics) {
                Map<String, Object> location = (Map<String, Object>) diagnostic.get("location");

                ClangWarning clangWarning = new ClangWarning();
                clangWarning.setCategory((String) diagnostic.get("category"));
                // file is an integer representing the index of the file in the files array
                clangWarning.setFile(new File(files.get((Integer) location.get("file"))));
                clangWarning.setLine((Integer) location.get("line"));
                clangWarning.setType((String) diagnostic.get("type"));

                result.add(clangWarning);
            }
        } catch (final IOException e) {
            LOGGER.error("Error processing file named {}", file, e);
        } catch (final ParserConfigurationException e) {
            LOGGER.error("Error processing file named {}", file, e);
        } catch (final ParseException e) {
            LOGGER.error("Error processing file named {}", file, e);
        } catch (final SAXException e) {
            LOGGER.error("Error processing file named {}", file, e);
        } catch (final PropertyListFormatException e) {
            LOGGER.error("Error processing file named {}", file, e);
        }

        return result;
    }
}
