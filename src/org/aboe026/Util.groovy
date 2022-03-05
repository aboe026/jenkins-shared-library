package org.aboe026

import org.codehaus.groovy.runtime.StackTraceUtils

/** Contains helper methods
  */
class Util {

    @NonCPS
    static String getMethodName(def steps) {
        def temp = Thread.currentThread().getStackTrace()
        temp.each { tem ->
            steps.println "TEST tem.getMethodName: '${tem.getMethodName()}'"
            // [3].getMethodName())
        }
        return ''
    }

}
