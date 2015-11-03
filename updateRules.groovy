// Update rules.txt and profile-clint.xml from OCLint documentation
// Priority is determined from the category

@Grab(group='org.codehaus.groovy.modules.http-builder',
        module='http-builder', version='0.7')

import groovyx.net.http.*
import groovy.util.XmlParser
import groovy.xml.MarkupBuilder

// Files
def rulesXml() {
    new File('src/main/resources/org/sonar/plugins/objectivec/rules-oclint.xml')
}
def profileXml() {
    new File('src/main/resources/org/sonar/plugins/objectivec/profile-oclint.xml')
}

def splitCamelCase(value) {
    value.replaceAll(
            String.format("%s|%s|%s",
                    "(?<=[A-Z])(?=[A-Z][a-z])",
                    "(?<=[^A-Z])(?=[A-Z])",
                    "(?<=[A-Za-z])(?=[^A-Za-z])"
            ),
            " "
    ).toLowerCase()
}


def parseCategory(url, name, priority) {
    def rules = new XmlParser().parse(rulesXml())

    def http = new HTTPBuilder(url)
    def html = http.get([:])

    def root = html.'**'.find { it.@id.toString().contains(name) }
    root.'DIV'.each { rule ->
        def ruleName =  splitCamelCase(rule.H2.text() - '¶').capitalize()

        // Original name
        def nameInSource = null
        try {
            def sourceUrl = rule."**".find { it.name() == 'A' && it.text().contains('oclint-rules/rules') }.@href.toString()

            // Fixes busted URLs in docs
            sourceUrl = sourceUrl.replace('EmptyElseStatementRule.cpp', 'EmptyElseBlockRule.cpp')
            sourceUrl = sourceUrl.replace('RedundantNilCheck.cpp', 'RedundantNilCheckRule.cpp')

            def sourceHttp = new HTTPBuilder(sourceUrl)
            def sourceHtml = sourceHttp.get[:]

            def found = sourceHtml."**".find {it.name() == 'TR' && it.text().contains("return\"")}.text()
            def match = found =~ /"([^"]*)"/
            nameInSource = match[0][1]

        } catch (Exception e) {

        }

        if (nameInSource != null) {

            // Overrides for key not being properly detected in source
            if (ruleName == "Broken nil check") {
                nameInSource = "broken nil check"
            }
            if (ruleName == "Misplaced nil check") {
                nameInSource = "misplaced nil check"
            }

            def existingRule = rules.rule.find { it.key.text() == nameInSource }

            if (existingRule) {
                existingRule.name[0].value = ruleName
                existingRule.description[0].value = rule.P[1].text()
                // Keep existing priority
            } else {
                def newRule = rules.appendNode('rule')
                newRule.appendNode('key').value = nameInSource
                newRule.appendNode('name').value = ruleName
                newRule.appendNode('priority').value = priority
                newRule.appendNode('description').value = rule.P[1].text()
            }

            println "Retrieved rule ${nameInSource}"
        } else {
            println "Unable to retrieve rule with name ${ruleName}"
        }
    }

    def writer = new StringWriter()
    def printer = new groovy.util.XmlNodePrinter(new IndentPrinter(writer, "    "))
    printer.setPreserveWhitespace true
    printer.print(rules)
    rulesXml().text = writer.toString()
}

def writeProfileOCLint() {
    def rulesXml = new XmlParser().parse(rulesXml())

    def writer = new StringWriter()
    MarkupBuilder xml = new MarkupBuilder(new IndentPrinter(writer, "    "))
    xml.profile() {
        name "OCLint"
        language "objc"
        rules {
            rulesXml.rule.each { rl ->
                rule {
                    repositoryKey "OCLint"
                    key rl.key.text()
                }
            }
        }
    }

    profileXml().text = writer.toString()
}


// Parse OCLint online documentation
parseCategory("http://docs.oclint.org/en/dev/rules/basic.html", "basic", "CRITICAL")
parseCategory("http://docs.oclint.org/en/dev/rules/convention.html", "convention", "MAJOR")
parseCategory("http://docs.oclint.org/en/dev/rules/empty.html", "empty", "CRITICAL")
parseCategory("http://docs.oclint.org/en/dev/rules/migration.html", "migration", "MINOR")
parseCategory("http://docs.oclint.org/en/dev/rules/naming.html", "naming", "MAJOR")
parseCategory("http://docs.oclint.org/en/dev/rules/redundant.html", "redundant", "MINOR")
parseCategory("http://docs.oclint.org/en/dev/rules/size.html", "size", "CRITICAL")
parseCategory("http://docs.oclint.org/en/dev/rules/unused.html", "unused", "INFO")

writeProfileOCLint()