/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.ParameterValidator
import spock.lang.Specification

class ParameterValidator__requiredSpec extends Specification {

    def 'If params null and no constructor param, throws exception for method'() {
        when:
        ParameterValidator.required(null, 'foo', 'bar')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" method: Must be Map with at least "bar" property defined.'
    }

    def 'If params null and constructor false, throws exception for method'() {
        when:
        ParameterValidator.required(null, 'foo', 'bar', false)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" method: Must be Map with at least "bar" property defined.'
    }

    def 'If params null and constructor true, throws exception for constructor'() {
        when:
        ParameterValidator.required(null, 'foo', 'bar', true)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" constructor: Must be Map with at least "bar" property defined.'
    }

    def 'If params empty and no constructor param, throws exception for method'() {
        when:
        ParameterValidator.required([:], 'foo', 'bar')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" method: Must be Map with at least "bar" property defined.'
    }

    def 'If params empty and constructor false, throws exception for method'() {
        when:
        ParameterValidator.required([:], 'foo', 'bar', false)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" method: Must be Map with at least "bar" property defined.'
    }

    def 'If params empty and constructor true, throws exception for constructor'() {
        when:
        ParameterValidator.required([:], 'foo', 'bar', true)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "foo" constructor: Must be Map with at least "bar" property defined.'
    }

    def 'If params without prop and no constructor param, throws exception for method'() {
        when:
        ParameterValidator.required([ hello: 'world' ], 'foo', 'bar')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" method: Must have property "bar" with non-null value.'
    }

    def 'If params without prop and constructor false, throws exception for method'() {
        when:
        ParameterValidator.required([ hello: 'world' ], 'foo', 'bar', false)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" method: Must have property "bar" with non-null value.'
    }

    def 'If params without prop and constructor true, throws exception for constructor'() {
        when:
        ParameterValidator.required([ hello: 'world' ], 'foo', 'bar', true)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" constructor: Must have property "bar" with non-null value.'
    }

    def 'If params with null prop and no constructor param, throws exception for method'() {
        when:
        ParameterValidator.required([ bar: null ], 'foo', 'bar')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" method: Must have property "bar" with non-null value.'
    }

    def 'If params with null prop and constructor false, throws exception for method'() {
        when:
        ParameterValidator.required([ bar: null ], 'foo', 'bar', false)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" method: Must have property "bar" with non-null value.'
    }

    def 'If params with null prop and constructor true, throws exception for constructor'() {
        when:
        ParameterValidator.required([ bar: null ], 'foo', 'bar', true)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "foo" constructor: Must have property "bar" with non-null value.'
    }

    def 'If params with prop, no exception thrown'() {
        when:
        ParameterValidator.required([ bar: 'world' ], 'foo', 'bar', true)

        then:
        notThrown(Exception)
    }

}

class ParameterValidator__requiredWithConstructorFallbackSpec extends Specification {

    def 'If params null and not defined by constructor, throws exception'() {
        when:
        ParameterValidator.requiredWithConstructorFallback([:], null, 'hello', 'foo')

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter passed to "hello" method: Must be Map with at least "foo" property defined or have "foo" passed into "LinkedHashMap" constructor.'
    }

    def 'If params empty and not defined by constructor, throws exception'() {
        when:
        ParameterValidator.requiredWithConstructorFallback([:], [:], 'hello', 'foo')

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter passed to "hello" method: Must be Map with at least "foo" property defined or have "foo" passed into "LinkedHashMap" constructor.'
    }

    def 'If params does not contain property and not defined by constructor, throws exception'() {
        when:
        ParameterValidator.requiredWithConstructorFallback([:], [ world: 'bar' ], 'hello', 'foo')

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter passed to "hello" method: Must be Map with at least "foo" property defined or have "foo" passed into "LinkedHashMap" constructor.'
    }

    def 'If params contains property and not defined by constructor, return params property'() {
        expect:
        ParameterValidator.requiredWithConstructorFallback([:], [ foo: 'bar' ], 'hello', 'foo') == 'bar'
    }

    def 'If params contains property and defined by constructor, return params property'() {
        expect:
        ParameterValidator.requiredWithConstructorFallback([ foo: 'world' ], [ foo: 'bar' ], 'hello', 'foo') == 'bar'
    }

    def 'If params does not contain property and defined by constructor, return constructor property'() {
        expect:
        ParameterValidator.requiredWithConstructorFallback([ foo: 'bar' ], [:], 'hello', 'foo') == 'bar'
    }

}

class ParameterValidator__enumerableSpec extends Specification {

    def 'If allowed null, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', null)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "allowedValues" for method "enumerable" called by method "hello":  Must be non-empty array.'
    }

    def 'If allowed empty, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', [])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "allowedValues" for method "enumerable" called by method "hello":  Must be non-empty array.'
    }

    def 'If not in single allowed, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', ['world'])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "foo" with method "hello": Must be one of "world".'
    }

    def 'If not in double allowed, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', ['world', 'lorem'])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "foo" with method "hello": Must be one of "world|lorem".'
    }

    def 'If double not in single allowed, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'bat']], 'hello', 'foo', ['world'])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "foo" with method "hello": Must be one of "world".'
    }

    def 'If double not in double allowed, throws exception'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'bat']], 'hello', 'foo', ['world', 'lorem'])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "foo" with method "hello": Must be one of "world|lorem".'
    }

    def 'If in single allowed, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', ['bar'])

        then:
        notThrown(Exception)
    }

    def 'If in double allowed first, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', ['bar', 'world'])

        then:
        notThrown(Exception)
    }

    def 'If in double allowed second, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: 'bar'], 'hello', 'foo', ['world', 'bar'])

        then:
        notThrown(Exception)
    }

    def 'If doubles match allowed, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'bat']], 'hello', 'foo', ['bar', 'bat'])

        then:
        notThrown(Exception)
    }

    def 'If doubles reverse allowed, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'bat']], 'hello', 'foo', ['bat', 'bar'])

        then:
        notThrown(Exception)
    }

    def 'If doubles in triple allowed first and middle, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'bat']], 'hello', 'foo', ['bar', 'bat', 'baz'])

        then:
        notThrown(Exception)
    }

    def 'If doubles in triple allowed first and last, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: ['bar', 'baz']], 'hello', 'foo', ['bar', 'bat', 'baz'])

        then:
        notThrown(Exception)
    }

    def 'If doubles in triple allowed middle and last, no exception thrown'() {
        when:
        ParameterValidator.enumerable([ foo: ['bat', 'baz']], 'hello', 'foo', ['bar', 'bat', 'baz'])

        then:
        notThrown(Exception)
    }

}

class ParameterValidator__defaultIfNotSetSpec extends Specification {

    def 'If params null, default returned'() {
        expect:
        ParameterValidator.defaultIfNotSet(null, 'foo', 'bar') == 'bar'
    }

    def 'If params empty, default returned'() {
        expect:
        ParameterValidator.defaultIfNotSet([:], 'foo', 'bar') == 'bar'
    }

    def 'If params property null, default returned'() {
        expect:
        ParameterValidator.defaultIfNotSet([ foo: null ], 'foo', 'bar') == 'bar'
    }

    def 'If params property empty, default returned'() {
        expect:
        ParameterValidator.defaultIfNotSet([ foo: '' ], 'foo', 'bar') == 'bar'
    }

    def 'If params property non-null, default ignored'() {
        expect:
        ParameterValidator.defaultIfNotSet([ foo: 'world' ], 'foo', 'bar') == 'world'
    }

}

class ParameterValidator__isArraySpec extends Specification {

    def 'If no params, returns false'() {
        expect:
        ParameterValidator.isArray() == false
    }

    def 'If null param, returns false'() {
        expect:
        ParameterValidator.isArray(null) == false
    }

    def 'If empty string param, returns false'() {
        expect:
        ParameterValidator.isArray('') == false
    }

    def 'If 0 param, returns false'() {
        expect:
        ParameterValidator.isArray(0) == false
    }

    def 'If string param, returns false'() {
        expect:
        ParameterValidator.isArray('notarray') == false
    }

    def 'If integer param, returns false'() {
        expect:
        ParameterValidator.isArray(42) == false
    }

    def 'If empty map param, returns false'() {
        expect:
        ParameterValidator.isArray([:]) == false
    }

    def 'If empty array param, returns true'() {
        expect:
        ParameterValidator.isArray([]) == true
    }

    def 'If single string array param, returns true'() {
        expect:
        ParameterValidator.isArray(['hello']) == true
    }

    def 'If single integer array param, returns true'() {
        expect:
        ParameterValidator.isArray([42]) == true
    }

    def 'If single boolean array param, returns true'() {
        expect:
        ParameterValidator.isArray([false]) == true
    }

    def 'If multiple string array param, returns true'() {
        expect:
        ParameterValidator.isArray(['hello', 'world']) == true
    }

    def 'If multiple integer array param, returns true'() {
        expect:
        ParameterValidator.isArray([42, 2043]) == true
    }

    def 'If multiple boolean array param, returns true'() {
        expect:
        ParameterValidator.isArray([false, true]) == true
    }

}
