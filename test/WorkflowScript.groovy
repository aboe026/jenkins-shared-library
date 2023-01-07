package org.aboe026

import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

/** Class provided by Jenkins at execution time
  */
abstract class WorkflowScript extends CpsScript { // groovylint-disable-line AbstractClassWithoutAbstractMethod

    Map getEnv() {
        return [
            BUILD_URL: 'https://mock-workflow-script:440/build/url'
        ]
    }

    Map getCurrentBuild() {
        return [
            currentResult: 'to be mocked'
        ]
    }

    Map string(Map map) {
        return map
    }

    void build(Map map) { } // groovylint-disable-line BuilderMethodWithSideEffects, EmptyMethodInAbstractClass, FactoryMethodName, UnusedMethodParameter

    ResponseContentSupplier httpRequest(Map map) { // groovylint-disable-line UnusedMethodParameter
        return new ResponseContentSupplier('to be mocked', 200)
    }

    JSONObject readJSON(Map map) {
        return JSONObject.fromObject(map.text)
    }

    String sh(Map map) { // groovylint-disable-line UnusedMethodParameter
        return 'to be mocked'
    }

}
