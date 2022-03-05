package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    @NonCPS
    static String getMethodName(def steps) {
        def marker = new Throwable()
        StackTraceUtils.sanitize(marker).stackTrace.eachWithIndex { e, i ->
            steps.println "> $i ${e.toString().padRight(30)} ${e.methodName}"
        }
        return ''
    }

}
