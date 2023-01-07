/* groovylint-disable ClassNameSameAsFilename, ClassJavadoc, NoDef, VariableTypeRequired, MethodName, MethodReturnTypeRequired, JUnitPublicNonTestMethod */

import net.sf.json.JSONObject
import org.aboe026.DockerUtil
import org.aboe026.WorkflowScript
import spock.lang.Specification

class DockerUtil__constructorSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new DockerUtil()

        then:
        def exception = thrown(Exception)
        exception.message == 'Empty constructor not valid for DockerUtil class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).'
    }

    def 'If null, throws exception'() {
        when:
        WorkflowScript steps = null
        new DockerUtil(steps)

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "DockerUtil" constructor: Must be Map with at least "steps" property defined.'
    }

    def 'If just Script, returns DockerUtil'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new DockerUtil(steps)

        then:
        badges.steps == steps
    }

    def 'If empty Map, throws exception'() {
        when:
        new DockerUtil([:])

        then:
        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter "null" passed to "DockerUtil" constructor: Must be Map with at least "steps" property defined.'
    }

    def 'If Map without steps, throws exception'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        new DockerUtil(foo: steps)

        then:
        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "DockerUtil" constructor: Must have property "steps" with non-null value.'
    }

    def 'If null in map, throws exception'() {
        when:
        WorkflowScript steps = null
        new DockerUtil(
            steps: steps
        )

        then:
        def exception = thrown(Exception)
        exception.message == 'Invalid parameter passed to "DockerUtil" constructor: Must have property "steps" with non-null value.'
    }

    def 'If Map with just steps, returns DockerUtil'() {
        when:
        WorkflowScript steps = Stub(WorkflowScript)
        def badges = new DockerUtil(steps: steps)

        then:
        badges.steps == steps
    }

}

class DockerUtil__getHostMountDirSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new DockerUtil(steps: Stub(WorkflowScript)).getHostMountDir()

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter "null" passed to "getHostMountDir" method: Must be Map with at least "workDir" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new DockerUtil(steps: Stub(WorkflowScript)).getHostMountDir([:])

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter "null" passed to "getHostMountDir" method: Must be Map with at least "workDir" property defined.'
    }

    def 'If getContainerDetails null, throws error'() {
        DockerUtil dockerUtil
        String mountDestination = '/var/jenkins_home'
        String workSubdir = 'work-dir'
        String containerName = 'cicd-jenkins-1'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        dockerUtil.getHostMountDir(workDir: "${mountDestination}/${workSubdir}")

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> null
        def exception = thrown(Exception)
        exception.message ==
            "Could not find mount source for mount destination '${mountDestination}' in container '${containerName}'"
    }

    def 'If just workDir param and no mount destination match, throws error'() {
        DockerUtil dockerUtil
        String mountDestination = '/var/jenkins_home'
        String workSubdir = 'work-dir'
        String mountSource = 'D:\\Repos\\repository'
        String containerName = 'cicd-jenkins-1'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        dockerUtil.getHostMountDir(workDir: "${mountDestination}/${workSubdir}")

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Destination: 'no-match',
                    Source: mountSource
                ]
            ]
        ])
        def exception = thrown(Exception)
        exception.message ==
            "Could not find mount source for mount destination '${mountDestination}' in container '${containerName}'"
    }

    def 'If all params and no mount destination match, throws error'() {
        DockerUtil dockerUtil
        String mountDestination = '/path/to/no/match'
        String workSubdir = 'work-dir'
        String mountSource = 'D:\\Repos\\repository'
        String containerName = 'custom-name-no-match'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        dockerUtil.getHostMountDir(
            workDir: "${mountDestination}/${workSubdir}",
            containerName: containerName,
            mountDestination: mountDestination
        )

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Destination: 'no-match',
                    Source: mountSource
                ]
            ]
        ])
        def exception = thrown(Exception)
        exception.message ==
            "Could not find mount source for mount destination '${mountDestination}' in container '${containerName}'"
    }

    def 'If just workDir param and matching mount destination, gets mount dir with default params'() {
        DockerUtil dockerUtil
        String mountDestination = '/var/jenkins_home'
        String workSubdir = 'work-dir'
        String mountSource = 'D:\\Repos\\repository'
        String containerName = 'cicd-jenkins-1'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def hostMountDir = dockerUtil.getHostMountDir(workDir: "${mountDestination}/${workSubdir}")

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Destination: mountDestination,
                    Source: mountSource
                ]
            ]
        ])
        hostMountDir == "D:\\\\Repos\\\\repository/${workSubdir}"
    }

    def 'If workDir and containerName params with matching mount destination, gets mount dir with correct params'() {
        DockerUtil dockerUtil
        String mountDestination = '/var/jenkins_home'
        String workSubdir = 'work-dir'
        String mountSource = 'D:\\Repos\\repository'
        String containerName = 'custom-container-name'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def hostMountDir = dockerUtil.getHostMountDir(
            workDir: "${mountDestination}/${workSubdir}",
            containerName: containerName
        )

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Destination: mountDestination,
                    Source: mountSource
                ]
            ]
        ])
        hostMountDir == "D:\\\\Repos\\\\repository/${workSubdir}"
    }

    def 'If all params with matching mount destination, gets mount dir with correct params'() {
        DockerUtil dockerUtil
        String mountDestination = '/custom/dir/path'
        String workSubdir = 'work-dir'
        String mountSource = 'D:\\Repos\\repository'
        String containerName = 'custom-container-name'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def hostMountDir = dockerUtil.getHostMountDir(
            workDir: "${mountDestination}/${workSubdir}",
            containerName: containerName,
            mountDestination: mountDestination
        )

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Destination: mountDestination,
                    Source: mountSource
                ]
            ]
        ])
        hostMountDir == "D:\\\\Repos\\\\repository/${workSubdir}"
    }

}

class DockerUtil__getContainerVolumesSpec extends Specification {

    def 'If no parameters, throws exception'() {
        when:
        new DockerUtil(steps: Stub(WorkflowScript)).getContainerVolumes()

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter "null" passed to "getContainerVolumes" method: Must be Map with at least "containerName" property defined.'
    }

    def 'If Map empty, throws exception'() {
        when:
        new DockerUtil(steps: Stub(WorkflowScript)).getContainerVolumes([:])

        then:
        def exception = thrown(Exception)
        exception.message ==
            'Invalid parameter "null" passed to "getContainerVolumes" method: Must be Map with at least "containerName" property defined.'
    }

    def 'If getContainerDetails null, returns empy list'() {
        DockerUtil dockerUtil
        String containerName = 'my-container'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def volumes = dockerUtil.getContainerVolumes(containerName: containerName)

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> null
        volumes == []
    }

    def 'If no mount of type volume, returns empty list'() {
        DockerUtil dockerUtil
        String containerName = 'my-container'
        String volumeName = 'my-volume'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def volumes = dockerUtil.getContainerVolumes(containerName: containerName)

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Type: 'not-volume',
                    Name: volumeName
                ]
            ]
        ])
        volumes == []
    }

    def 'If single mount of type volume, returns single list'() {
        DockerUtil dockerUtil
        String containerName = 'my-container'
        String volumeName = 'my-volume'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def volumes = dockerUtil.getContainerVolumes(containerName: containerName)

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Type: 'volume',
                    Name: volumeName
                ]
            ]
        ])
        volumes == [volumeName]
    }

    def 'If multiple mount of type volume, returns multiple list'() {
        DockerUtil dockerUtil
        String containerName = 'my-container'
        String volumeNameOne = 'my-volume-one'
        String volumeNameTwo = 'my-volume-two'

        given:
        dockerUtil = Spy(DockerUtil, constructorArgs: [Spy(WorkflowScript)])

        when:
        def volumes = dockerUtil.getContainerVolumes(containerName: containerName)

        then:
        1 * dockerUtil.getContainerDetails { argument ->
            argument == containerName
        } >> JSONObject.fromObject([
            Mounts: [
                [
                    Type: 'volume',
                    Name: volumeNameOne
                ],
                [
                    Type: 'volume',
                    Name: volumeNameTwo
                ]
            ]
        ])
        volumes == [volumeNameOne, volumeNameTwo]
    }

}

class DockerUtil__getContainerDetailsSpec extends Specification {

    def 'if container does not exist, throws error'() {
        WorkflowScript steps
        String containerName = 'cicd-jenkins-1'

        given:
        steps = Spy(WorkflowScript)

        when:
        new DockerUtil(steps).getContainerDetails(containerName)

        then:
        1 * steps.sh { argument ->
            argument == [
                script: "docker inspect ${containerName} || echo ''",
                returnStdout: true
            ]
        } >> '[]'
        0 * steps.readJSON
        def exception = thrown(Exception)
        exception.message == "Invalid container name '${containerName}': does not exist"
    }

    def 'if container is invalid JSON, throws error'() {
        WorkflowScript steps
        String containerName = 'cicd-jenkins-1'
        String invalidJson = '''{
            "hello": "world
        }toast'''

        given:
        steps = Spy(WorkflowScript)

        when:
        new DockerUtil(steps).getContainerDetails(containerName)

        then:
        1 * steps.sh { argument ->
            argument == [
                script: "docker inspect ${containerName} || echo ''",
                returnStdout: true
            ]
        } >> invalidJson
        0 * steps.readJSON
        def exception = thrown(Exception)
        exception.message ==
            "Error reading JSON '${invalidJson}' for container '${containerName}': Found starting '{' but missing '}' at the end. at character 0 of null"
    }

    def 'if container exists, returns JSON representation'() {
        WorkflowScript steps
        JSONObject containerJson
        String containerName = 'cicd-jenkins-1'
        String expectedString = """{
            "Id": "05230e557c044294e11e13111a215a1bd5be384faa051b324b0863d772b4f271",
            "State": {
                "Status": "running"
            },
            "Image": "sha256:49176f190c7e9cdb51ac85ab6c6d5e4512352218190cd69b08e6fd803ffbf3da",
            "Name": "${containerName}",
            "Mounts": [
                {
                    "Destination": "mount-dest",
                    "Source": "mount-source"
                }
            ]
        }"""
        JSONObject expectedJson = JSONObject.fromObject([
            Id: '05230e557c044294e11e13111a215a1bd5be384faa051b324b0863d772b4f271',
            State: [
                Status: 'running'
            ],
            Image: 'sha256:49176f190c7e9cdb51ac85ab6c6d5e4512352218190cd69b08e6fd803ffbf3da',
            Name: containerName,
            Mounts: [
                [
                    Destination: 'mount-dest',
                    Source: 'mount-source'
                ]
            ]
        ])

        given:
        steps = Spy(WorkflowScript)

        when:
        containerJson = new DockerUtil(steps).getContainerDetails(containerName)

        then:
        1 * steps.sh { argument ->
            argument == [
                script: "docker inspect ${containerName} || echo ''",
                returnStdout: true
            ]
        } >> expectedString
        1 * steps.readJSON { argument ->
            argument == [
                text: expectedString
            ]
        } >> expectedJson
        containerJson == expectedJson
    }

}
