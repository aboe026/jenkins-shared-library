/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import jenkins.plugins.http_request.ResponseContentSupplier
import org.aboe026.ShieldsIoBadges
import org.aboe026.WorkflowScript
import spock.lang.Specification

class ShieldsIoBadges__constructorSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new ShieldsIoBadges()

        then:
        def exception = thrown(Exception)
        exception.message == 'Empty constructor not valid for ShieldsIoBadges class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).'
    }

    def 'If null, throws exception'() {
        when:
        WorkflowScript steps = null
        new ShieldsIoBadges(steps)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "ShieldsIoBadges" constructor: Must be Map with at least "steps" property defined.'
    }

    def 'If null with job, throws exception'() {
        when:
        WorkflowScript steps = null
        new ShieldsIoBadges(steps, '/foo/bar')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid first parameter "null" passed to "ShieldsIoBadges" constructor: Must be non-null Script object.'
    }

    def 'If just Script, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(steps)

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Script with null job, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(steps, null)

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Script with empty job, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(steps, '')

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Script with job, returns ShieldsIoBadges with job specified'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(steps, '/foo/bar')

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/foo/bar'
    }

    def 'If empty Map, throws exception'() {
        when:
        new ShieldsIoBadges([:])

        then:
        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "ShieldsIoBadges" constructor: Must be Map with at least "steps" property defined.'
    }

    def 'If Map without steps, throws exception'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        new ShieldsIoBadges(foo: steps)

        then:
        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "ShieldsIoBadges" constructor: Must have property "steps" with non-null value.'
    }

    def 'If Map without job, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(steps: steps)

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Map with null job, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(
            steps: steps,
            setBadgeResultsJob: null
        )

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Map with empty job, returns ShieldsIoBadges with default job'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(
            steps: steps,
            setBadgeResultsJob: ''
        )

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/shields.io-badge-results/set-badge-result'
    }

    def 'If Map with job, returns ShieldsIoBadges with job specified'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new ShieldsIoBadges(
            steps: steps,
            setBadgeResultsJob: '/foo/bar'
        )

        then:
        badges.steps == steps
        badges.setBadgeResultsJob == '/foo/bar'
    }

}

class ShieldsIoBadges__uploadBuildResultSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult()

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadBuildResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult([:])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadBuildResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map with invalid result, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult(
            repo: 'foo',
            result: 'bar'
        )

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "result" with method "uploadBuildResult": Must be one of "ABORTED|FAILURE|NOT_BUILT|SUCCESS|UNSTABLE".'
    }

    def 'If Map with success result, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'SUCCESS'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'passing'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with unstable result, triggers build with yellow badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'UNSTABLE'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'unstable'],
                    [name: 'color', value: 'yellow']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with not build result, triggers build with grey badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'NOT_BUILT'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'none'],
                    [name: 'color', value: 'lightgrey']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with aborted result, triggers build with orange badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'ABORTED'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'aborted'],
                    [name: 'color', value: 'orange']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with failed result, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'FAILURE'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'failed'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with explicit branch and result, triggers build with non-default branch and result'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)
        steps.getCurrentBuild() >> [
            currentResult: 'FAILURE'
        ]

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo',
            result: 'SUCCESS',
            branch: 'master'
        )

        then:
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'master'],
                    [name: 'label', value: 'build'],
                    [name: 'message', value: 'passing'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

}

class ShieldsIoBadges__uploadCoberturaCoverageResultSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadCoberturaCoverageResult()

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadCoberturaCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadCoberturaCoverageResult([:])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadCoberturaCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map with repo and 100 coverage, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 50,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '100%'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 90 coverage, triggers build with green badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 40,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '90%'],
                    [name: 'color', value: 'green']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 80 coverage, triggers build with yellowgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 30,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '80%'],
                    [name: 'color', value: 'yellowgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 70 coverage, triggers build with yellow badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 20,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '70%'],
                    [name: 'color', value: 'yellow']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 60 coverage, triggers build with orange badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 10,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '60%'],
                    [name: 'color', value: 'orange']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 50 coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 0,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '50%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 0 coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 0,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 0,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '0%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and no coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {}
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '0%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with explicity params and 95 coverage, triggers build with green badge and non-default params'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo',
            branch: 'master',
            credentialsId: 'SUPER_SECRET',
            buildUrl: 'https://overwritten-build-url:444/custom/url'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://overwritten-build-url:444/custom/url/cobertura/api/json?depth=2',
                authentication: 'SUPER_SECRET',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 45,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'master'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '95%'],
                    [name: 'color', value: 'green']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with ignoreCategories on bad coverage, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoberturaCoverageResult(
            repo: 'foo',
            ignoreCategories: ['Methods']
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "name": "Lines",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Conditionals",
                            "numerator": 50,
                            "denominator": 50
                        },{
                            "name": "Methods",
                            "numerator": 10,
                            "denominator": 50
                        }
                    ]
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '100%'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

}

class ShieldsIoBadges__uploadJacocoCoverageResultSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadJacocoCoverageResult()

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadJacocoCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadJacocoCoverageResult([:])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadJacocoCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map with repo and 100 coverage, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 50,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '100%'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 90 coverage, triggers build with green badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 40,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '90%'],
                    [name: 'color', value: 'green']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 80 coverage, triggers build with yellowgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 30,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '80%'],
                    [name: 'color', value: 'yellowgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 70 coverage, triggers build with yellow badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 20,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '70%'],
                    [name: 'color', value: 'yellow']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 60 coverage, triggers build with orange badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 10,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '60%'],
                    [name: 'color', value: 'orange']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 50 coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 0,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '50%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and 0 coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 0,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 0,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '0%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with repo and no coverage, triggers build with red badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '{}',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '0%'],
                    [name: 'color', value: 'red']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with explicity params and 95 coverage, triggers build with green badge and non-default params'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo',
            branch: 'master',
            credentialsId: 'SUPER_SECRET',
            buildUrl: 'https://overwritten-build-url:444/custom/url'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://overwritten-build-url:444/custom/url/jacoco/api/json',
                authentication: 'SUPER_SECRET',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 45,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'master'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '95%'],
                    [name: 'color', value: 'green']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

    def 'If Map with ignoreCategories on bad coverage, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadJacocoCoverageResult(
            repo: 'foo',
            ignoreCategories: ['instructionCoverage']
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/jacoco/api/json',
                authentication: 'JENKINS_CREDENTIALS',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "branchCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "lineCoverage": {
                    "covered": 50,
                    "total": 50
                },
                "instructionCoverage": {
                    "covered": 10,
                    "total": 50
                }
            }''',
            200
        )
        1 * steps.build { argument ->
            argument == [
                job: '/shields.io-badge-results/set-badge-result',
                parameters: [
                    [name: 'repo', value: 'foo'],
                    [name: 'branch', value: 'main'],
                    [name: 'label', value: 'coverage'],
                    [name: 'message', value: '100%'],
                    [name: 'color', value: 'brightgreen']
                ],
                quietPeriod: 0,
                wait: false
            ]
        }
    }

}

class ShieldsIoBadges__isCategoryIgnoredSpec extends Specification {

    def 'If ignores null, returns false'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored(null, 'hello') == false
    }

    def 'If ignores empty, returns false'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored([], 'hello') == false
    }

    def 'If ignores does not contain category, returns false'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored(['world'], 'hello') == false
    }

    def 'If ignores contains only category, returns true'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored(['hello'], 'hello') == true
    }

    def 'If ignores contains category first, returns true'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored(['hello', 'world'], 'hello') == true
    }

    def 'If ignores contains category last, returns true'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).isCategoryIgnored(['world', 'hello'], 'hello') == true
    }

}

class ShieldsIoBadges__getAveragePercentageSpec extends Specification {

    def 'If called with empty array, 0 returned'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).getAveragePercentage([]) == 0
    }

    def 'If called with single array, array value returned'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).getAveragePercentage([0.1]) == 10
    }

    def 'If called with multiple array, average array value returned'() {
        expect:
        new ShieldsIoBadges(Stub(WorkflowScript)).getAveragePercentage([0.1, 0.2]) == 15
    }

}
