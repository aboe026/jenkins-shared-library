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

// class ShieldsIoBadges__uploadBuildResultSpec extends Specification {

//     def 'If no parameters, throws exception'() {
//         when:
//         new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult()

//         then:
//         def exception = thrown(Exception)
//         exception.message == 'Invalid parameter "null" passed to "uploadBuildResult" method: Must be Map with at least "repo" property defined.'
//     }

//     def 'If Map empty, throws exception'() {
//         when:
//         new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult([:])

//         then:
//         def exception = thrown(Exception)
//         exception.message == 'Invalid parameter "null" passed to "uploadBuildResult" method: Must be Map with at least "repo" property defined.'
//     }

//     def 'If Map with just repo, throws exception'() {
//         when:
//         new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult(repo: 'foo')

//         then:
//         def exception = thrown(Exception)
//         exception.message == 'Invalid parameter passed to "uploadBuildResult" method: Must have property "status" with non-null value.'
//     }

//     def 'If Map with invalid status, throws exception'() {
//         when:
//         new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult(
//             repo: 'foo',
//             status: 'bar'
//         )

//         then:
//         def exception = thrown(Exception)
//         exception.message == 'Invalid value "bar" for parameter "status" with method "uploadBuildResult": Must be one of "ABORTED|FAILURE|NOT_BUILT|SUCCESS|UNSTABLE".'
//     }

//     def 'If Map with success status, triggers build with brightgreen badge and defaults'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'SUCCESS'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'main'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'passing'],
//                     [name: 'color', value: 'brightgreen']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

//     def 'If Map with unstable status, triggers build with yellow badge and defaults'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'UNSTABLE'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'main'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'unstable'],
//                     [name: 'color', value: 'yellow']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

//     def 'If Map with not build status, triggers build with grey badge and defaults'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'NOT_BUILT'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'main'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'none'],
//                     [name: 'color', value: 'lightgrey']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

//     def 'If Map with aborted status, triggers build with orange badge and defaults'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'ABORTED'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'main'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'aborted'],
//                     [name: 'color', value: 'orange']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

//     def 'If Map with failed status, triggers build with red badge and defaults'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'FAILURE'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'main'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'failed'],
//                     [name: 'color', value: 'red']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

//     def 'If Map with explicit branch, triggers build with non-default branch'() {
//         WorkflowScript steps

//         given:
//         steps = Spy(WorkflowScript)

//         when:
//         new ShieldsIoBadges(steps).uploadBuildResult(
//             repo: 'foo',
//             status: 'SUCCESS',
//             branch: 'master'
//         )

//         then:
//         1 * steps.build { argument ->
//             argument == [
//                 job: '/shields.io-badge-results/set-badge-result',
//                 parameters: [
//                     [name: 'repo', value: 'foo'],
//                     [name: 'branch', value: 'master'],
//                     [name: 'label', value: 'build'],
//                     [name: 'message', value: 'passing'],
//                     [name: 'color', value: 'brightgreen']
//                 ],
//                 quietPeriod: 0,
//                 wait: false
//             ]
//         }
//     }

// }

class ShieldsIoBadges__uploadCoverageResultSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadCoverageResult()

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadCoverageResult([:])

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "uploadCoverageResult" method: Must be Map with at least "repo" property defined.'
    }

    def 'If Map with repo and 100 coverage, triggers build with brightgreen badge and defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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
        new ShieldsIoBadges(steps).uploadCoverageResult(
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
                            "numerator": 50,
                            "denominator": 50
                        },{
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

    def 'If Map with explicity params and 95 coverage, triggers build with green badge and non-default params'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadCoverageResult(
            repo: 'foo',
            branch: 'master',
            credentialsId: 'SUPER_SECRET'
        )

        then:
        1 * steps.httpRequest { argument ->
            argument == [
                url: 'https://mock-workflow-script:440/build/url/cobertura/api/json?depth=2',
                authentication: 'SUPER_SECRET',
                quiet: true
            ]
        } >> new ResponseContentSupplier(
            '''{
                "results": {
                    "elements": [
                        {
                            "numerator": 50,
                            "denominator": 50
                        },{
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

}

class ShieldsIoBadges__colorSpec extends Specification {

    def 'BRIGHT_GREEN returns brightgreen'() {
        expect:
        ShieldsIoBadges.Color.BRIGHT_GREEN.toString() == 'brightgreen'
    }

    def 'GREEN returns green'() {
        expect:
        ShieldsIoBadges.Color.GREEN.toString() == 'green'
    }

    def 'YELLOW_GREEN returns yellowgreen'() {
        expect:
        ShieldsIoBadges.Color.YELLOW_GREEN.toString() == 'yellowgreen'
    }

    def 'YELLOW returns yellow'() {
        expect:
        ShieldsIoBadges.Color.YELLOW.toString() == 'yellow'
    }

    def 'ORANGE returns orange'() {
        expect:
        ShieldsIoBadges.Color.ORANGE.toString() == 'orange'
    }

    def 'RED returns red'() {
        expect:
        ShieldsIoBadges.Color.RED.toString() == 'red'
    }

    def 'BLUE returns blue'() {
        expect:
        ShieldsIoBadges.Color.BLUE.toString() == 'blue'
    }

    def 'LIGHT_GREY returns lightgrey'() {
        expect:
        ShieldsIoBadges.Color.LIGHT_GREY.toString() == 'lightgrey'
    }

}
