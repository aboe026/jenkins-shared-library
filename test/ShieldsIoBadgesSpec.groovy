/* groovylint-disable ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import org.aboe026.ShieldsIoBadges
import org.aboe026.WorkflowScript
import spock.lang.Specification

class ShieldsIoBadges__ConstructorSpec extends Specification {

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

    def 'If Map with just repo, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult(repo: 'foo')

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "uploadBuildResult" method: Must have property "status" with non-null value.'
    }

    def 'If Map with invalid status, throws exception'() {
        when:
        new ShieldsIoBadges(steps: Stub(WorkflowScript)).uploadBuildResult(
            repo: 'foo',
            status: 'bar'
        )

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid value "bar" for parameter "status" with method "uploadBuildResult": Must be one of "ABORTED|FAILURE|NOT_BUILT|SUCCESS|UNSTABLE".'
    }

    def 'If Map with success status, creates green badge with defaults'() {
        WorkflowScript steps

        given:
        steps = Spy(WorkflowScript)

        when:
        new ShieldsIoBadges(steps).uploadBuildResult(
            repo: 'foo',
            status: 'SUCCESS'
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

}
