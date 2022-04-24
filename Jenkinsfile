/* groovylint-disable NoDef, VariableTypeRequired */
node {
    def workDir = "${WORKSPACE}/${env.BRANCH_NAME}-${env.BUILD_ID}"
    def lintImage = 'nvuillam/npm-groovy-lint'
    def gradleImage = 'gradle:7.4'
    def exceptionThrown = false
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
                            jacoco(
                                execPattern: 'build/jacoco/*.exec',
                                classPattern: 'build/classes/groovy/main',
                                sourcePattern: 'src'
                            )

                            // then use jacoco endpoint
                            // http://localhost:8080/job/jenkins-shared-library/job/jenkins-shared-library/view/change-requests/job/PR-4/5/jacoco/api/json?pretty=true
                            // to call for nums/denoms
                            // rename uploadCoverage to uploadJacocoCoverage and uploadCoberturaCoverage
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
