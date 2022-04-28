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
        List<String> allowedStringValues = []
        allowedValues.each { value ->
            allowedStringValues.add(value.toString())
        }
        def received = ParameterValidator.isArray(params[propertyName]) ? params[propertyName] : [params[propertyName]] // groovylint-disable-line NoDef, VariableTypeRequired
        received.each { value ->
            if (!allowedStringValues.contains(value.toString())) {
                throw new Exception(
                    "Invalid value \"${value}\" for parameter \"${propertyName}\" with method \"${methodName}\": Must be one of \"${allowedStringValues.join('|')}\"."
                )
            }
        }
    }

    @NonCPS
    static String defaultIfNotSet(Map params, String propertyName, String defaultValue) {
        // for some reason the following
        //
        // if (!params || params[propertyName] == null || params[propertyName] == '') {
        //     return defaultValue
        // }
        //
        // caused jacoco to think there were gaps in code coverage,
        // so had to split the single if statement into multiple ones
        if (!params) {
            return defaultValue
        }
        if (params[propertyName] == null) {
            return defaultValue
        }
        if (params[propertyName] == '') {
            return defaultValue
        }
        return params[propertyName]
    }

    @NonCPS
    private static boolean isArray(def object) { // groovylint-disable-line MethodParameterTypeRequired, NoDef
        return [Collection, Object[]].any { arrayClass ->
            arrayClass.isAssignableFrom(object.getClass())
        }
    }

}
