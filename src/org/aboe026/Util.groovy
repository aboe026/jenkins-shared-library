package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    static String extractMethodName() {
        Throwable marker = new Throwable()
        return StackTraceUtils.sanitize(marker).stackTrace[1].methodName
    }

}
