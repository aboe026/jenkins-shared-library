package org.aboe026

/** Validate method Map input parameters
  */
class ParameterValidator {

    @NonCPS
    static void required(Map params, String methodName, String propertyName) {
        def received = params[propertyName] // groovylint-disable-line NoDef, VariableTypeRequired
        if (received == null) {
            throw new Exception("Invalid value \"${received}\" for parameter \"${propertyName}\" with method \"${methodName}\": Must be non-null.")
        }
    }

    @NonCPS
    static void enumerable(Map params, String methodName, String propertyName, String[] allowedValues) {
        def received = params[propertyName] // groovylint-disable-line NoDef, VariableTypeRequired
        if (!allowedValues.contains(received)) {
            throw new Exception("Invalid value \"${received}\" for parameter \"${propertyName}\" with method \"${methodName}\": Must be one of \"${allowedValues.join('|')}\".")
        }
    }

    @NonCPS
    static void applyDefault(Map params, String propertyName, String defaultValue) {
        if (params[propertyName] == null) {
            params[propertyName] = defaultValue
        }
    }

}
