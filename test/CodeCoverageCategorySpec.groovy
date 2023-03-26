/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.CodeCoverageCategory
import spock.lang.Specification

class CodeCoverageCategorySpec extends Specification {

    def 'BRANCH toString returns branch'() {
        expect:
        CodeCoverageCategory.BRANCH.toString() == 'branch'
    }

    def 'CLASS toString returns class'() {
        expect:
        CodeCoverageCategory.CLASS.toString() == 'class'
    }

    def 'FILE toString returns file'() {
        expect:
        CodeCoverageCategory.FILE.toString() == 'file'
    }

    def 'INSTRUCTION toString returns instruction'() {
        expect:
        CodeCoverageCategory.INSTRUCTION.toString() == 'instruction'
    }

    def 'LINE toString returns line'() {
        expect:
        CodeCoverageCategory.LINE.toString() == 'line'
    }

    def 'METHOD toString returns method'() {
        expect:
        CodeCoverageCategory.METHOD.toString() == 'method'
    }

    def 'MODULE toString returns module'() {
        expect:
        CodeCoverageCategory.MODULE.toString() == 'module'
    }

    def 'PACKAGE toString returns package'() {
        expect:
        CodeCoverageCategory.PACKAGE.toString() == 'package'
    }

}
