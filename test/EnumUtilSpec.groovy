/* groovylint-disable ClassJavadoc, ClassNameSameAsFilename, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import com.cloudbees.groovy.cps.NonCPS
import org.aboe026.EnumUtil
import spock.lang.Specification

class EnumUtil__getValuesSpec extends Specification {

    def 'returns single string in array for Enum with single enumeration'() {
        expect:
        EnumUtil.getValues(SingleEnum) == ['hello']
    }

    def 'returns multiple strings in array for Enum with multiple enumerations'() {
        expect:
        EnumUtil.getValues(MultiEnum) == ['foo', 'bar']
    }

}

enum SingleEnum {

    HELLO('hello'),

    final String value

    private SingleEnum(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}

enum MultiEnum {

    FOO('foo'),
    BAR('bar'),

    final String value

    private MultiEnum(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}
