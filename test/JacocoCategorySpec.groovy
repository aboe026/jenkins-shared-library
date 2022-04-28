/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.JacocoCategory
import spock.lang.Specification

class JacocoCategorySpec extends Specification {

    def 'BRANCH_COVERAGE toString returns branchCoverage'() {
        expect:
        JacocoCategory.BRANCH_COVERAGE.toString() == 'branchCoverage'
    }

    def 'CLASS_COVERAGE toString returns classCoverage'() {
        expect:
        JacocoCategory.CLASS_COVERAGE.toString() == 'classCoverage'
    }

    def 'COMPLEXITY_SCORE toString returns complexityScore'() {
        expect:
        JacocoCategory.COMPLEXITY_SCORE.toString() == 'complexityScore'
    }

    def 'INSTRUCTION_COVERAGE toString returns instructionCoverage'() {
        expect:
        JacocoCategory.INSTRUCTION_COVERAGE.toString() == 'instructionCoverage'
    }

    def 'LINE_COVERAGE toString returns lineCoverage'() {
        expect:
        JacocoCategory.LINE_COVERAGE.toString() == 'lineCoverage'
    }

    def 'METHOD_COVERAGE toString returns methodCoverage'() {
        expect:
        JacocoCategory.METHOD_COVERAGE.toString() == 'methodCoverage'
    }

}
