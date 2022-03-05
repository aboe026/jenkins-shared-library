package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    @NonCPS
    static String getMethodName() {
        String methodName = ''
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace()
        for (int i = 0; i < stacktrace.length; i++) {
            if (stacktrace[i].getMethodName() == 'getMethodName') {
                methodName = stacktrace[i + 1].getMethodName()
                break
            }
        }
        return methodName
    }

}
