package org.aboe026

import com.cloudbees.groovy.cps.NonCPS

/** Validate method Map input parameters
  */
class ParameterValidator {

    @NonCPS
    static void required(Map params, String methodName, String propertyName, boolean constructor = false) {
        String type = constructor ? 'constructor' : 'method'
        if (!params) {
            throw new Exception("Invalid parameter \"null\" passed to \"${methodName}\" ${type}: Must be Map with at least \"${propertyName}\" property defined.")
        }
        if (params[propertyName] == null) {
            throw new Exception("Invalid parameter passed to \"${methodName}\" ${type}: Must have property \"${propertyName}\" with non-null value.")
        }
    }

    @NonCPS
    static void enumerable(Map params, String methodName, String propertyName, List<String> allowedValues) {
        if (!allowedValues) {
            throw new Exception("Invalid parameter \"allowedValues\" for method \"enumerable\" called by method \"${methodName}\":  Must be non-empty array.")
        }
        def received = params[propertyName] // groovylint-disable-line NoDef, VariableTypeRequired
        if (!allowedValues.contains(received)) {
            throw new Exception("Invalid value \"${received}\" for parameter \"${propertyName}\" with method \"${methodName}\": Must be one of \"${allowedValues.join('|')}\".")
        }
    }

    @NonCPS
    static String defaultIfNotSet(Map params, String propertyName, String defaultValue) {
        if (!params || params[propertyName] == null) {
            return defaultValue
        }
        return params[propertyName]
    }

}
