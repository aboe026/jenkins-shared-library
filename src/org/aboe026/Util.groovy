package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    @NonCPS
    static String extractMethodName() {
        Throwable marker = new Throwable()
        println 'TEST extractMethodName StackTraceUtils.sanitize(marker):'
        println StackTraceUtils.sanitize(marker)
        return StackTraceUtils.sanitize(marker).stackTrace[1].methodName
    }

}
