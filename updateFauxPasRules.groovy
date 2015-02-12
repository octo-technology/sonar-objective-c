// Update profile-fauxpas.xml from Faux Pas online rules documentation
// Severity is determined from the category

@Grab(group='org.codehaus.groovy.modules.http-builder',
        module='http-builder', version='0.7')

import groovyx.net.http.*
import groovy.xml.MarkupBuilder
import groovy.json.JsonBuilder

def parseRules(url, catMapping) {

    def result = []

    def http = new HTTPBuilder(url)
    http.contentEncoding = ContentEncoding.Type.GZIP
    def html = http.get(contentType : 'text/html;charset=UTF-8')

    def categories = html."**".findAll {it.@class.toString().contains('tag-section')}
    categories.each {cat ->

        def rules = cat."**".findAll {it.@class.toString().contains('rule')}
        rules.each {r ->

            def rule = [
                    category: cat.H2.text(),
                    key: r."**".find {it.@class.toString().contains("short-name")}.text(),
                    name: r.H3.text().trim().replaceAll('\\n', ' '),
                    description: r."**".find {it.@class.toString().contains("description")}.text().trim().replaceAll('\\n', ' '),
                    severity: catMapping[cat.H2.text()]
            ]

            result.add(rule)
        }
    }



    return result
}

def writeProfileFauxPas(rls, file) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.profile() {
        name "FauxPas"
        language "objc"
        rules {
            rls.each {rl ->
                rule {
                    repositoryKey "FauxPas"
                    key rl.key
                }
            }
        }
    }

    file.text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + writer.toString()

}

def writeRules(rls, file) {

    def builder = new JsonBuilder()
    builder(rls)

    file.text = builder.toPrettyString()

}

// Files
File rulesJson = new File('src/main/resources/org/sonar/plugins/fauxpas/rules.json')
File profileXml = new File('src/main/resources/org/sonar/plugins/fauxpas/profile-fauxpas.xml')

// Parse online documentation
def rules = parseRules('http://fauxpasapp.com/rules/', [
        BestPractice: 'MAJOR',
        Resources: 'MAJOR',
        Config: 'MINOR',
        Localization: 'MAJOR',
        APIUsage: 'CRITICAL',
        VCS: 'INFO',
        Style: 'MAJOR',
        Pedantic: 'MINOR',
        Miscellaneous: 'MINOR'
])

// Write profile
writeProfileFauxPas(rules, profileXml)

// Write rules
writeRules(rules, rulesJson)