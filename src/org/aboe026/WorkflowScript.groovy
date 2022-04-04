package org.aboe026

import org.jenkinsci.plugins.workflow.cps.CpsScript

/** Class provided by Jenkins at execution time
  */
abstract class WorkflowScript extends CpsScript { // groovylint-disable-line AbstractClassWithoutAbstractMethod

    Map string(Map map) {
        return map
    }

    void build(Map map) { } // groovylint-disable-line BuilderMethodWithSideEffects, EmptyMethodInAbstractClass, FactoryMethodName, UnusedMethodParameter

}
