package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    @NonCPS
    static String getMethodName(def steps) {
        String methodName = ''
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace()
        steps.println("TEST stacktrace.length: '${stacktrace.length}'")
        for (int i = 0; i < stacktrace.length; i++) {
            steps.println "TEST i: '${i}'"
            steps.println "TEST stacktrace[i].getMethodName(): '${stacktrace[i].getMethodName()}'"
            if (stacktrace[i].getMethodName() == 'getMethodName') {
                methodName = stacktrace[i + 1].getMethodName()
                break
            }
        }
        return methodName
    }

}
