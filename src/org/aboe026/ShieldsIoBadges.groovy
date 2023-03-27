package org.aboe026

import hudson.model.Result
import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONObject

/** Manage shields.io badge results
  */
class ShieldsIoBadges implements Serializable {

    private static final long serialVersionUID = 1L
    private final Script steps
    private final String repo
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
        this(steps, null)
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.ShieldsIoBadges
     *
     *      def badges = new ShieldsIoBadges(this, 'repo-name')
     *
     *      or
     *
     *      def badges = new ShieldsIoBadges(this, 'repo-name', '/path/in/jenkins/to/set-badge-result-job')
     */
    ShieldsIoBadges(Script steps, String repo, String setBadgeResultsJob = '/shields.io-badge-results/set-badge-result') {
        if (!steps) {
            throw new Exception("Invalid first parameter \"${steps}\" passed to \"ShieldsIoBadges\" constructor: Must be non-null Script object.")
        }
        this.steps = steps
        this.repo = repo
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
     *          repo: 'repo-name',
     *          setBadgeResultsJob: '/path/in/jenkins/to/set-badge-result-job'
     *      )
     */
    ShieldsIoBadges(Map params) {
        ParameterValidator.required(params, 'ShieldsIoBadges', 'steps', true)
        this.steps = params.steps
        this.repo = params.repo
        this.setBadgeResultsJob = ParameterValidator.defaultIfNotSet(params, 'setBadgeResultsJob', '/shields.io-badge-results/set-badge-result')
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadBuildResult(
     *             repo: 'data-structures',
     *             branch: env.BRANCH_NAME,
     *             result: currentBuild.currentResult
     *         )
     *     }
     */
    void uploadBuildResult(Map params) {
        String repo = ParameterValidator.requiredWithConstructorFallback(this, params, 'uploadBuildResult', 'repo')
        if (params?.result) {
            ParameterValidator.enumerable(params, 'uploadBuildResult', 'result', [
                Result.ABORTED.toString(),
                Result.FAILURE.toString(),
                Result.NOT_BUILT.toString(),
                Result.SUCCESS.toString(),
                Result.UNSTABLE.toString(),
            ])
        }
        String result = ParameterValidator.defaultIfNotSet(params, 'result', this.steps.currentBuild.currentResult)
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
                this.steps.string(name: 'repo', value: repo),
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
        List<String> coverageCategories = EnumUtil.getValues(CoberturaCategory)
        if (params && params.ignoreCategories) {
            ParameterValidator.enumerable(params, 'uploadCoberturaCoverageResult', 'ignoreCategories', coverageCategories)
        }
        // groovylint-disable-next-line ClosureAsLastMethodParameter
        getAndUploadCoverageResult(params, 'uploadCoberturaCoverageResult', '/cobertura/api/json?depth=2', { JSONObject json ->
            List<BigDecimal> averages = []
            Closure addCategory = { JSONObject result ->
                if (!this.isCategoryIgnored(params?.ignoreCategories, result.name)) {
                    averages.add(result.numerator / result.denominator)
                }
            }
            if (json?.results?.elements) {
                json.results.elements.each { result ->
                    addCategory(result)
                }
            }
            return getAveragePercentage(averages)
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
        List<String> coverageCategories = EnumUtil.getValues(JacocoCategory)
        if (params && params.ignoreCategories) {
            ParameterValidator.enumerable(params, 'uploadJacocoCoverageResult', 'ignoreCategories', coverageCategories)
        }
        getAndUploadCoverageResult(params, 'uploadJacocoCoverageResult', '/jacoco/api/json', { JSONObject json -> // groovylint-disable-line ClosureAsLastMethodParameter
            List<BigDecimal> averages = []
            Closure addCategory = { String category ->
                JSONObject categoryObject = json[category]
                if (categoryObject) {
                    if (!this.isCategoryIgnored(params?.ignoreCategories, category)) {
                        averages.add(categoryObject.covered / categoryObject.total)
                    }
                }
            }
            if (json) {
                coverageCategories.each { String coverageCategory ->
                    addCategory(coverageCategory)
                }
            }
            return getAveragePercentage(averages)
        })
    }

    /* Can be called in Jenkinsfile like:
     *
     *     if (env.BRANCH_NAME == 'main') {
     *         badges.uploadCoverageResult(
     *             repo: 'data-structures',
     *             branch: env.BRANCH_NAME,
     *             ignoreCategories: ['instruction']
     *         )
     *     }
     */
    void uploadCoverageResult(Map params) {
        List<String> coverageCategories = EnumUtil.getValues(CodeCoverageCategory)
        if (params && params.ignoreCategories) {
            ParameterValidator.enumerable(params, 'uploadCoverageResult', 'ignoreCategories', coverageCategories)
        }
        getAndUploadCoverageResult(params, 'uploadCoverageResult', '/coverage/api/json', { JSONObject json -> // groovylint-disable-line ClosureAsLastMethodParameter
            List<BigDecimal> averages = []
            Closure addCategory = { String category ->
                String categoryObject = json.projectStatistics[category]
                if (categoryObject) {
                    if (!this.isCategoryIgnored(params?.ignoreCategories, category)) {
                        averages.add(new BigDecimal(categoryObject.replace('%', '')) / 100)
                    }
                }
            }
            if (json?.projectStatistics) {
                coverageCategories.each { String coverageCategory ->
                    addCategory(coverageCategory)
                }
            }
            return getAveragePercentage(averages)
        })
    }

    private void getAndUploadCoverageResult(Map params, String method, String resultsUrlPath, Closure getPercentageFromJson) {
        String repo = ParameterValidator.requiredWithConstructorFallback(this, params, method, 'repo')
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

        int percentage = getPercentageFromJson(coverageJson)

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
                this.steps.string(name: 'repo', value: repo),
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

    private int getAveragePercentage(List<BigDecimal> averages) {
        if (averages.size() == 0) {
            return 0
        }
        BigDecimal total = 0
        averages.each { average ->
            total += average
        }
        return Math.floor(total / averages.size() * 100)
    }

}
