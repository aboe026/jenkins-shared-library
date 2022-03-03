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

    ShieldsIoBadges() {
        throw new Exception('Empty constructor not valid for ShieldsIoBadges class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).')
    }

    ShieldsIoBadges(Script steps) {
        if (steps == null) {
            throw new Exception('Steps passed to ShieldsIoBadges constructor must be non-null.')
        }
        this.steps = steps
    }

    ShieldsIoBadges(Map params) {
        println 'TEST ShieldsIoBadges params:'
        println params
        ParameterValidator.required(params, Util.extractMethodName(), 'steps')
        this.steps = params.steps
        this.setBadgeResultsJob = ParameterValidator.defaultIfNotSet(params, 'setBadgeResultsJob', '/shields.io-badge-results/set-badge-result')
    }

    void uploadBuildResult(Map params) {
        ParameterValidator.required(params, Util.extractMethodName(), 'status')
        ParameterValidator.required(params, Util.extractMethodName(), 'repo')
        ParameterValidator.enumerable(params, Util.extractMethodName(), 'status', [
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
            wait: true
        )
    }

    // TODO: other things to upload?
    void uploadCoverageResult(Map params) {
        ParameterValidator.required(params, Util.extractMethodName(), 'repo')
        String branch = ParameterValidator.defaultIfNotSet(params, 'branch', 'main')
        String credentialsId = ParameterValidator.defaultIfNotSet(params, 'credentialsId', 'JENKINS_CREDENTIALS')

        println "TEST this.steps.env.BUILD_URL: '${this.steps.env.BUILD_URL}'"

        println 'TEST params:'
        println params

        URL buildUrl = new URL(this.steps.env.BUILD_URL)
        URL coverageUrl = new URL(buildUrl.protocol(), buildUrl.host(), buildUrl.port(), buildUrl.path() + '/cobertura/api/json?depth=2', null)

        ResponseContentSupplier response
        DynamicSecret.asVariable(this.steps, credentialsId) { String secretVariable ->
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
        int percentage = Math.round(Math.floor(overallCoverage * 100))
        println "TEST percent: '${percentage}'"
        String color = ''
        switch (true) {
            case percentage = 100:
                color = Color.BRIGHT_GREEN
                break
            case percentage >= 90:
                color = Color.GREEN
                break
            case percentage >= 80:
                color = Color.YELLOW_GREEN
                break
            case percentage >= 70:
                color = Color.YELLOW
                break
            case percentage >= 60:
                color = Color.ORANGE
                break
            default:
                color = Color.RED
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
            wait: true
        )
    }

}

enum Color {

  BRIGHT_GREEN {

    @Override
    String toString() {
      return 'brightgreen'
    }

  },

  GREEN {

    @Override
    String toString() {
      return 'green'
    }

  },

  YELLOW_GREEN {

    @Override
    String toString() {
      return 'yellowgreen'
    }

  },

  YELLOW {

    @Override
    String toString() {
      return 'yellow'
    }

  },

  ORANGE {

    @Override
    String toString() {
      return 'orange'
    }

  },

  RED {

    @Override
    String toString() {
      return 'red'
    }

  },

  BLUE {

    @Override
    String toString() {
      return 'blue'
    }

  },

  LIGHT_GREY {

    @Override
    String toString() {
      return 'lightgrey'
    }

  }

}