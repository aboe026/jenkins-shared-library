/* groovylint-disable NoDef, VariableTypeRequired */
@Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable

import org.aboe026.ShieldsIoBadges

node {
    def workDir = "${WORKSPACE}/${env.BRANCH_NAME}-${env.BUILD_ID}"
    def lintImage = 'nvuillam/npm-groovy-lint'
    def gradleImage = 'gradle:7.4'
    def exceptionThrown = false
    def badges = new ShieldsIoBadges(this, 'jenkins-shared-library')
    def uploadBadges = env.BRANCH_NAME == 'main'

    try {
        ansiColor('xterm') {
            dir(workDir) {
                stage('Pull Runtime Image') {
                    sh "docker pull ${lintImage}"
                    sh "docker pull ${gradleImage}"
                }

                stage('Checkout') {
                    checkout scm
                }

                stage('Lint') {
                    docker.image(lintImage).inside('--entrypoint=') {
                        sh 'npm-groovy-lint --ignorepattern "**/bin/**,**/build/**,**/.gradle/**" --failon info'
                    }
                }

                stage('Test') {
                    docker.image(gradleImage).inside {
                        try {
                            sh './gradlew test'
                            sh './gradlew jacocoTestReport'
                        } finally {
                            junit testResults: 'build/test-results/test/TESTS-TestSuitesMerged.xml', allowEmptyResults: true
                            recordCoverage(
                                skipPublishingChecks: true,
                                sourceCodeRetention: 'EVERY_BUILD',
                                tools: [
                                    [
                                        parser: 'JACOCO',
                                        pattern: 'build\\reports\\jacoco\\test\\jacocoTestReport.xml'
                                    ]
                                ]
                            )
                            if (uploadBadges) {
                                badges.uploadJacocoCoverageResult(
                                    branch: env.BRANCH_NAME,
                                    ignoreCategories: ['instructionCoverage']
                                )
                            }
                        }
                    }
                }
            }
        }
    } catch (err) {
        exceptionThrown = true
        println 'Exception was caught in try block of jenkins job.'
        println err
    } finally {
        if (uploadBadges) {
            badges.uploadBuildResult(
                branch: env.BRANCH_NAME
            )
        }
        stage('Cleanup') {
            try {
                sh "rm -rf ${workDir}"
            } catch (err) {
                println 'Exception deleting working directory'
                println err
            }
            try {
                sh "rm -rf ${workDir}@tmp"
            } catch (err) {
                println 'Exception deleting temporary working directory'
                println err
            }
            if (exceptionThrown) {
                error('Exception was thrown earlier')
            }
        }
    }
}
