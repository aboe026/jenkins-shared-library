package org.aboe026

import hudson.model.Result
import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONObject

/** Manage shields.io badge results
  */
class ShieldsIoBadges implements Serializable {

    private static final long serialVersionUID = 1L
    Script steps
    String setBadgeResultsJob

    ShieldsIoBadges(Map params) {
        ParameterValidator.required(params, 'ShieldsIoBadges constructor', 'steps')
        ParameterValidator.applyDefault(params, 'setBadgeResultsJob', '/shields.io-badge-results/set-badge-result')
        this.steps = params.steps
        this.setBadgeResultsJob = params.setBadgeResultsJob
    }

    void uploadBuildResult(Map params) {
        ParameterValidator.required(params, 'setBuild', 'status')
        ParameterValidator.required(params, 'setBuild', 'repo')
        ParameterValidator.enumerable(params, 'setBuild', 'status', [
            Result.ABORTED.toString(),
            Result.FAILURE.toString(),
            Result.NOT_BUILT.toString(),
            Result.SUCCESS.toString(),
            Result.UNSTABLE.toString(),
        ])
        ParameterValidator.applyDefault(params, 'branch', 'main')
        String message = ''
        String color = ''
        switch (params.status) {
            case Result.SUCCESS.toString():
                message = 'passing'
                color = 'green'
                break
            case Result.UNSTABLE.toString():
                message = 'unstable'
                color = 'yellow'
                break
            case Result.NOT_BUILT.toString():
                message = 'none'
                color = 'lightgrey'
                break
            case Result.ABORTED.toString():
                message = 'aborted'
                color = 'orange'
                break
            default:
                message = 'failed'
                color = 'red'
                break
        }
        this.steps.build(
            job: this.setBadgeResultsJob,
            parameters: [
                this.steps.string(name: 'repo', value: params.repo),
                this.steps.string(name: 'branch', value: params.branch),
                this.steps.string(name: 'label', value: 'build'),
                this.steps.string(name: 'message', value: message),
                this.steps.string(name: 'color', value: color),
            ],
            quietPeriod: 0,
            wait: true
        )
    }

    void uploadCoverageResult(Map params) {
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
