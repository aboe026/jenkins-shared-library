/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.CoberturaCategory
import spock.lang.Specification

class CoberturaCategorySpec extends Specification {

    def 'CLASSES toString returns Classes'() {
        expect:
        CoberturaCategory.CLASSES.toString() == 'Classes'
    }

    def 'CONDITIONALS toString returns Conditionals'() {
        expect:
        CoberturaCategory.CONDITIONALS.toString() == 'Conditionals'
    }

    def 'FILES toString returns Files'() {
        expect:
        CoberturaCategory.FILES.toString() == 'Files'
    }

    def 'LINES toString returns Lines'() {
        expect:
        CoberturaCategory.LINES.toString() == 'Lines'
    }

    def 'METHODS toString returns Methods'() {
        expect:
        CoberturaCategory.METHODS.toString() == 'Methods'
    }

    def 'PACKAGES toString returns Packages'() {
        expect:
        CoberturaCategory.PACKAGES.toString() == 'Packages'
    }

}
