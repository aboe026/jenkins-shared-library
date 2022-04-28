/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.ShieldsIoBadgeColor
import spock.lang.Specification

class ShieldsIoBadgeColorSpec extends Specification {

    def 'BRIGHT_GREEN toString returns brightgreen'() {
        expect:
        ShieldsIoBadgeColor.BRIGHT_GREEN.toString() == 'brightgreen'
    }

    def 'GREEN toString returns green'() {
        expect:
        ShieldsIoBadgeColor.GREEN.toString() == 'green'
    }

    def 'YELLOW_GREEN toString returns yellowgreen'() {
        expect:
        ShieldsIoBadgeColor.YELLOW_GREEN.toString() == 'yellowgreen'
    }

    def 'YELLOW toString returns yellow'() {
        expect:
        ShieldsIoBadgeColor.YELLOW.toString() == 'yellow'
    }

    def 'ORANGE toString returns orange'() {
        expect:
        ShieldsIoBadgeColor.ORANGE.toString() == 'orange'
    }

    def 'RED toString returns red'() {
        expect:
        ShieldsIoBadgeColor.RED.toString() == 'red'
    }

    def 'BLUE toString returns blue'() {
        expect:
        ShieldsIoBadgeColor.BLUE.toString() == 'blue'
    }

    def 'LIGHT_GREY toString returns lightgrey'() {
        expect:
        ShieldsIoBadgeColor.LIGHT_GREY.toString() == 'lightgrey'
    }

}
