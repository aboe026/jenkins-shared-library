/* groovylint-disable ClassNameSameAsFilename, ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.Xml
import spock.lang.Specification

class Xml__transformSpec extends Specification {

    def 'If empty string, throws exception'() {
        String xml = ''
        when:
        Xml.transform(xml) { }

        then:
        def exception = thrown(Exception)
        exception.message == "Error parsing '${xml}' as XML: Premature end of file."
    }

    def 'If not valid XML, throws exception'() {
        String xml = 'not valid xml'
        when:
        Xml.transform(xml) { }

        then:
        def exception = thrown(Exception)
        exception.message == "Error parsing '${xml}' as XML: Content is not allowed in prolog."
    }

    def 'If no closure, returns original XML'() {
        String xml = '<?xml version="1.0" encoding="UTF-8"?><hello foo="bar"/>\r\n'

        expect:
        Xml.transform(xml) { } == xml
    }

    def 'If closure modifies XML, modified XML returned'() {
        String xml = '<?xml version="1.0" encoding="UTF-8"?><hello foo="bar"/>\r\n'

        expect:
        Xml.transform(xml) { root ->
            root['@foo'] = 'world'
        } == '<?xml version="1.0" encoding="UTF-8"?><hello foo="world"/>\r\n'
    }

    def 'If closure modifies external variable, modifications persist outside closure'() {
        def foos = []
        String xml = '<?xml version="1.0" encoding="UTF-8"?><hello foo="bar"/>\r\n'

        when:
        Xml.transform(xml) { root ->
            foos.push(root['@foo'])
        }

        then:
        foos == ['bar']
    }

}
