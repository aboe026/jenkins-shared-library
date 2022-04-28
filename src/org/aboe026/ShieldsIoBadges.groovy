package org.aboe026

import hudson.model.Result
import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONObject

/** Manage shields.io badge results
  */
class ShieldsIoBadges implements Serializable {

    private static final long serialVersionUID = 1L
    private final Script steps
    private final String setBadgeResultsJob

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
        this(steps, '/shields.io-badge-results/set-badge-result')
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
        if (!steps) {
            throw new Exception("Invalid first parameter \"${steps}\" passed to \"ShieldsIoBadges\" constructor: Must be non-null Script object.")
        }
        this.steps = steps
        this.setBadgeResultsJob = setBadgeResultsJob ?: '/shields.io-badge-results/set-badge-result'
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
        ParameterValidator.required(params, 'uploadBuildResult', 'repo')
        if (params.result) {
            ParameterValidator.enumerable(params, 'uploadBuildResult', 'result', [
                Result.ABORTED.toString(),
                Result.FAILURE.toString(),
                Result.NOT_BUILT.toString(),
                Result.SUCCESS.toString(),
                Result.UNSTABLE.toString(),
            ])
        }
        String result = ParameterValidator.defaultIfNotSet(params, 'result', this.steps.currentBuild.result)
        String branch = ParameterValidator.defaultIfNotSet(params, 'branch', 'main')
        String message = ''
        String color = ''
        switch (result) {
            case Result.SUCCESS.toString():
                message = 'passing'
                color = ShieldsIoBadgeColor.BRIGHT_GREEN
                break
            case Result.UNSTABLE.toString():
                message = 'unstable'
                color = ShieldsIoBadgeColor.YELLOW
                break
            case Result.NOT_BUILT.toString():
                message = 'none'
                color = ShieldsIoBadgeColor.LIGHT_GREY
                break
            case Result.ABORTED.toString():
                message = 'aborted'
                color = ShieldsIoBadgeColor.ORANGE
                break
            default:
                message = 'failed'
                color = ShieldsIoBadgeColor.RED
                break
        }
        this.steps.build(
            job: this.setBadgeResultsJob,
            parameters: [
                this.steps.string(name: 'repo', value: params.repo),
                this.steps.string(name: 'branch', value: branch),
                this.steps.string(name: 'label', value: 'build'),
                this.steps.string(name: 'message', value: message),
                this.steps.string(name: 'color', value: color)
            ],
            quietPeriod: 0,
            wait: false
        )
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadCoberturaCoverageResult(
     *             repo: 'data-structures',
     *             branch: env.BRANCH_NAME
     *         )
     *     }
     */
    void uploadCoberturaCoverageResult(Map params) {
        if (params && params.ignoreCategories) {
            ParameterValidator.enumerable(params, 'uploadCoberturaCoverageResult', 'ignoreCategories', [
                CoberturaCategory.CLASSES,
                CoberturaCategory.CONDITIONALS,
                CoberturaCategory.FILES,
                CoberturaCategory.LINES,
                CoberturaCategory.METHODS,
                CoberturaCategory.PACKAGES
            ])
        }
        uploadCoverageResult(params, 'uploadCoberturaCoverageResult', '/cobertura/api/json?depth=2', { JSONObject json -> // groovylint-disable-line ClosureAsLastMethodParameter
            int numeratorTotal = 0
            int denominatorTotal = 0
            Closure addCategory = { JSONObject result ->
                if (!this.isCategoryIgnored(params.ignoreCategories, result.name)) {
                    numeratorTotal += result.numerator
                    denominatorTotal += result.denominator
                }
            }
            json.results.elements.each { result ->
                addCategory(result)
            }
            return [ numeratorTotal, denominatorTotal ]
        })
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadJacocoCoverageResult(
     *             repo: 'data-structures',
     *             branch: env.BRANCH_NAME,
     *             ignoreCategories: ['instructionCoverage']
     *         )
     *     }
     */
    void uploadJacocoCoverageResult(Map params) {
        if (params && params.ignoreCategories) {
            ParameterValidator.enumerable(params, 'uploadJacocoCoverageResult', 'ignoreCategories', [
                JacocoCategory.BRANCH_COVERAGE,
                JacocoCategory.CLASS_COVERAGE,
                JacocoCategory.COMPLEXITY_SCORE,
                JacocoCategory.INSTRUCTION_COVERAGE,
                JacocoCategory.LINE_COVERAGE,
                JacocoCategory.METHOD_COVERAGE
            ])
        }
        uploadCoverageResult(params, 'uploadJacocoCoverageResult', '/jacoco/api/json', { JSONObject json -> // groovylint-disable-line ClosureAsLastMethodParameter
            int numeratorTotal = 0
            int denominatorTotal = 0
            Closure addCategory = { JacocoCategory category ->
                JSONObject categoryObject = json[category.toString()]
                if (categoryObject) {
                    if (!this.isCategoryIgnored(params.ignoreCategories, category.toString())) {
                        numeratorTotal += categoryObject.covered
                        denominatorTotal += categoryObject.total
                    }
                }
            }
            addCategory(JacocoCategory.BRANCH_COVERAGE)
            addCategory(JacocoCategory.CLASS_COVERAGE)
            addCategory(JacocoCategory.COMPLEXITY_SCORE)
            addCategory(JacocoCategory.INSTRUCTION_COVERAGE)
            addCategory(JacocoCategory.LINE_COVERAGE)
            addCategory(JacocoCategory.METHOD_COVERAGE)
            return [numeratorTotal, denominatorTotal]
        })
    }

    private void uploadCoverageResult(Map params, String method, String resultsUrlPath, Closure jsonToNumDenom) {
        ParameterValidator.required(params, method, 'repo')
        String buildUrl = ParameterValidator.defaultIfNotSet(params, 'buildUrl', this.steps.env.BUILD_URL)
        String branch = ParameterValidator.defaultIfNotSet(params, 'branch', 'main')
        String credentialsId = ParameterValidator.defaultIfNotSet(params, 'credentialsId', 'JENKINS_CREDENTIALS')

        URL buildUrlObject = new URL(buildUrl)
        String coverageUrl = new URL(buildUrlObject.getProtocol(), buildUrlObject.getHost(), buildUrlObject.getPort(), buildUrlObject.getPath() + resultsUrlPath, null)

        ResponseContentSupplier response = this.steps.httpRequest(
            url: coverageUrl,
            authentication: credentialsId,
            quiet: true
        )
        JSONObject coverageJson = this.steps.readJSON text: response.content

        def (int numeratorTotal, int denominatorTotal) = jsonToNumDenom(coverageJson)

        int percentage = getCoveragePercentage(numeratorTotal, denominatorTotal)

        String color = ''
        switch (percentage) {
            case 100:
                color = ShieldsIoBadgeColor.BRIGHT_GREEN
                break
            case 90..100:
                color = ShieldsIoBadgeColor.GREEN
                break
            case 80..90:
                color = ShieldsIoBadgeColor.YELLOW_GREEN
                break
            case 70..80:
                color = ShieldsIoBadgeColor.YELLOW
                break
            case 60..70:
                color = ShieldsIoBadgeColor.ORANGE
                break
            default:
                color = ShieldsIoBadgeColor.RED
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

    private boolean isCategoryIgnored(List<String> ignoreCategories, String category) {
        return ignoreCategories && ignoreCategories.contains(category.toString())
    }

    private int getCoveragePercentage(int numerator, int denominator) {
        int resolvedNumerator = numerator > 0 ? numerator : 0
        int resolvedDenominator = denominator
        if (resolvedDenominator < 1) {
            resolvedNumerator = 0
            resolvedDenominator = 1
        }
        BigDecimal overallCoverage = resolvedNumerator / resolvedDenominator
        return Math.floor(overallCoverage * 100)
    }

}
