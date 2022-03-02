package org.aboe026

import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONObject

/** Manage shields.io badge results
  */
@CompileDynamic
class ShieldsIoBadges implements Serializable {

    private static final long serialVersionUID = 1L
    WorkflowScript steps
    String setBadgeResultsJob

    ShieldsIoBadges(Map params) {
        ParameterValidator.required(params, 'ShieldsIoBadges constructor', 'steps')
        ParameterValidator.applyDefault(params, 'setBadgeResultsJob', '/shields.io-badge-results/set-badge-result')
        this.steps = params.steps
        this.setBadgeResultsJob = params.setBadgeResultsJob
    }

    void setBuild(Map params) {
        ParameterValidator.required(params, 'setBuild', 'status')
        ParameterValidator.required(params, 'setBuild', 'repo')
        ParameterValidator.enumerable(params, 'setBuild', 'status', [
            hudson.model.Result.ABORTED.toString(),
            hudson.model.Result.FAILURE.toString(),
            hudson.model.Result.NOT_BUILT.toString(),
            hudson.model.Result.SUCCESS.toString(),
            hudson.model.Result.UNSTABLE.toString(),
        ])
        ParameterValidator.applyDefault(params, 'branch', 'main')
        String message = ''
        String color = ''
        switch (params.status) {
            case hudson.model.Result.SUCCESS.toString():
                message = 'passing'
                color = 'green'
                break
            case hudson.model.Result.UNSTABLE.toString():
                message = 'unstable'
                color = 'yellow'
                break
            case hudson.model.Result.NOT_BUILT.toString():
                message = 'none'
                color = 'lightgrey'
                break
            case hudson.model.Result.ABORTED.toString():
                message = 'aborted'
                color = 'orange'
                break
            default:
                message = 'failed'
                color = 'red'
                break
        }
        build(
            job: this.setBadgeResultsJob,
            parameters: [
                string(name: 'repo', value: params.repo),
                string(name: 'branch', value: params.branch),
                string(name: 'label', value: 'build'),
                string(name: 'message', value: message),
                string(name: 'color', value: color),
            ],
            quietPeriod: 0,
            wait: true
        )
    }

    void setCoverage(Map params) {
        ParameterValidator.required(params, 'setCoverage', 'buildUrl')
        ParameterValidator.applyDefault(params, 'credentialsId', 'JENKINS_CREDENTIALS')

        URL buildUrl = new URL(params.buildUrl)
        URL coverageUrl = new URL(buildUrl.protocol(), buildUrl.host(), buildUrl.port(), buildUrl.path() + '/cobertura/api/json?depth=2', null)

        ResponseContentSupplier response
        DynamicSecret.asVariable(this.steps, params.credentialsId) { String secretVariable ->
            response = this.steps.httpRequest(
                url: coverageUrl,
                authentication: secretVariable
            )
        }
        JSONObject coverageJson = this.steps.readJSON text: response.content
        println 'TEST coverageJson'
        println coverageJson

        int numeratorTotal = 0
        int denominatorTotal = 0
        coverageJson.results.elements.each { result ->
            numeratorTotal += result.numerator
            denominatorTotal += result.denominator
        }
        // numeratorTotal -= 100
        BigDecimal overallCoverage = numeratorTotal / denominatorTotal
        println "TEST overallCoverage: '${overallCoverage}'"
        String percentage = "${Math.round(Math.floor(overallCoverage * 100))}%"
        println "TEST percent: '${percentage}'"
    }

}
