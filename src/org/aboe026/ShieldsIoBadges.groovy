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

    // Not allowed, need script steps for method executions
    ShieldsIoBadges() {
        throw new Exception('Empty constructor not valid for ShieldsIoBadges class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).')
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.ShieldsIoBadges
     *
     *      def badges = new ShieldsIoBadges(this)
     */
    ShieldsIoBadges(Script steps) {
        if (steps == null) {
            throw new Exception('Invalid parameter "null" passed to "ShieldsIoBadges" constructor: Must be non-null Script object.')
        }
        this.steps = steps
        this.setBadgeResultsJob = '/shields.io-badge-results/set-badge-result'
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.ShieldsIoBadges
     *
     *      def badges = new ShieldsIoBadges(this, '/path/in/jenkins/to/set-badge-result-job')
     */
    ShieldsIoBadges(Script steps, String setBadgeResultsJob) {
        if (steps == null) {
            throw new Exception('Invalid first parameter "null" passed to "ShieldsIoBadges" constructor: Must be non-null Script object.')
        }
        this.steps = steps
        this.setBadgeResultsJob = setBadgeResultsJob != null ? setBadgeResultsJob : '/shields.io-badge-results/set-badge-result'
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.ShieldsIoBadges
     *
     *      def badges = new ShieldsIoBadges(
     *          steps: this,
     *          setBadgeResultsJob: '/path/in/jenkins/to/set-badge-result-job'
     *      )
     */
    ShieldsIoBadges(Map params) {
        ParameterValidator.required(params, 'ShieldsIoBadges', 'steps', true)
        this.steps = params.steps
        this.setBadgeResultsJob = ParameterValidator.defaultIfNotSet(params, 'setBadgeResultsJob', '/shields.io-badge-results/set-badge-result')
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadBuildResult(
     *             repo: 'data-structures',
     *             status: currentBuild.currentResult,
     *             branch: env.BRANCH_NAME
     *         )
     *     }
     */
    void uploadBuildResult(Map params) {
        ParameterValidator.required(params, 'uploadBuildResult', 'status')
        ParameterValidator.required(params, 'uploadBuildResult', 'repo')
        ParameterValidator.enumerable(params, 'uploadBuildResult', 'status', [
            Result.ABORTED.toString(),
            Result.FAILURE.toString(),
            Result.NOT_BUILT.toString(),
            Result.SUCCESS.toString(),
            Result.UNSTABLE.toString(),
        ])
        String branch = ParameterValidator.defaultIfNotSet(params, 'branch', 'main')
        String message = ''
        String color = ''
        switch (params.status) {
            case Result.SUCCESS.toString():
                message = 'passing'
                color = 'brightgreen'
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
                this.steps.string(name: 'branch', value: branch),
                this.steps.string(name: 'label', value: 'build'),
                this.steps.string(name: 'message', value: message),
                this.steps.string(name: 'color', value: color),
            ],
            quietPeriod: 0,
            wait: false
        )
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadCoverageResult(
     *             repo: 'data-structures',
     *             branch: env.BRANCH_NAME
     *         )
     *     }
     */
    void uploadCoverageResult(Map params) {
        ParameterValidator.required(params, 'uploadCoverageResult', 'repo')
        String branch = ParameterValidator.defaultIfNotSet(params, 'branch', 'main')
        String credentialsId = ParameterValidator.defaultIfNotSet(params, 'credentialsId', 'JENKINS_CREDENTIALS')

        URL buildUrl = new URL(this.steps.env.BUILD_URL)
        String coverageUrl = new URL(buildUrl.getProtocol(), buildUrl.getHost(), buildUrl.getPort(), buildUrl.getPath() + '/cobertura/api/json?depth=2', null)

        ResponseContentSupplier response = this.steps.httpRequest(
            url: coverageUrl,
            authentication: credentialsId,
            quiet: true
        )
        JSONObject coverageJson = this.steps.readJSON text: response.content

        int numeratorTotal = 0
        int denominatorTotal = 0
        coverageJson.results.elements.each { result ->
            numeratorTotal += result.numerator
            denominatorTotal += result.denominator
        }
        BigDecimal overallCoverage = numeratorTotal / denominatorTotal
        int percentage = Math.round(Math.floor(overallCoverage * 100))
        String color = ''
        switch (percentage) {
            case 100:
                color = Color.BRIGHT_GREEN
                break
            case 90..100:
                color = Color.GREEN
                break
            case 80..90:
                color = Color.YELLOW_GREEN
                break
            case 70..80:
                color = Color.YELLOW
                break
            case 60..70:
                color = Color.ORANGE
                break
            default:
                color = Color.RED
                break
        }
        this.steps.build(
            job: this.setBadgeResultsJob,
            parameters: [
                this.steps.string(name: 'repo', value: params.repo),
                this.steps.string(name: 'branch', value: branch),
                this.steps.string(name: 'label', value: 'coverage'),
                this.steps.string(name: 'message', value: "${percentage}%"),
                this.steps.string(name: 'color', value: color),
            ],
            quietPeriod: 0,
            wait: false
        )
    }

}

enum Color {

    BRIGHT_GREEN {

        @Override
        @NonCPS
        String toString() {
            return 'brightgreen'
        }

    },

    GREEN {

        @Override
        @NonCPS
        String toString() {
            return 'green'
        }

    },

    YELLOW_GREEN {

        @Override
        @NonCPS
        String toString() {
            return 'yellowgreen'
        }

    },

    YELLOW {

        @Override
        @NonCPS
        String toString() {
            return 'yellow'
        }

    },

    ORANGE {

        @Override
        @NonCPS
        String toString() {
            return 'orange'
        }

    },

    RED {

        @Override
        @NonCPS
        String toString() {
            return 'red'
        }

    },

    BLUE {

        @Override
        @NonCPS
        String toString() {
            return 'blue'
        }

    },

    LIGHT_GREY {

        @Override
        @NonCPS
        String toString() {
            return 'lightgrey'
        }

    }

}
