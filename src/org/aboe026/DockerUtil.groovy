package org.aboe026

import net.sf.json.JSONObject

/** Utility for interacting with Docker
  */
class DockerUtil implements Serializable {

    private static final long serialVersionUID = 1L
    private final Script steps

    // Not allowed, need script steps for method executions
    DockerUtil() {
        throw new Exception('Empty constructor not valid for DockerUtil class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).')
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.DockerUtil
     *
     *      def dockerUtil = new DockerUtil(this)
     */
    DockerUtil(Script steps) {
        this.steps = steps
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.DockerUtil
     *
     *      def dockerUtil = new DockerUtil(
     *          steps: this
     *      )
     */
    DockerUtil(Map params) {
        ParameterValidator.required(params, 'DockerUtil', 'steps', true)
        this.steps = params.steps
    }

    /* Can be called in Jenkinsfile like:
     *
     *     def workDir = "${WORKSPACE}/${env.BRANCH_NAME}-${env.BUILD_ID}"
     *     def mountDir = dockerUtil.getHostMountDir(workDir: workDir)
     */
    String getHostMountDir(Map params) {
        String workDir = ParameterValidator.required(params, 'getHostMountDir', 'workDir')
        String containerName = ParameterValidator.defaultIfNotSet(params, 'containerName', 'cicd-jenkins-1')
        String mountDestination = ParameterValidator.defaultIfNotSet(params, 'mountDestination', '/var/jenkins_home')
        JSONObject container = this.getContainerDetails(containerName)
        String mountSource = ''
        if (container?.Mounts) {
            container.Mounts.each { mount ->
                if (mount.Destination == mountDestination) {
                    mountSource = mount.Source.replaceAll('\\\\', '\\\\\\\\')
                }
            }
        }
        if (mountSource == '') {
            throw new Exception("Could not find mount source for mount destination '${mountDestination}' in container '${containerName}'")
        }
        return workDir.replace(mountDestination, mountSource)
    }

    /* Can be called in Jenkinsfile like:
     *
     *     def dockerVolumesToDelete = dockerUtil.getContainerVolumes(containerName: "${uniqueName}-database-1")
     */
    List<String> getContainerVolumes(Map params) {
        String containerName = ParameterValidator.required(params, 'getContainerVolumes', 'containerName')
        List<String> volumeNames = []
        JSONObject container = this.getContainerDetails(containerName)
        if (container?.Mounts) {
            container.Mounts.each { mount ->
                if (mount.Type == 'volume') {
                    volumeNames.add(mount.Name)
                }
            }
        }
        return volumeNames
    }

    JSONObject getContainerDetails(String containerName) {
        String containerString = this.steps.sh(
            script: "docker inspect ${containerName} || echo ''",
            returnStdout: true
        ).trim()
        if (containerString == '[]') {
            throw new Exception("Invalid container name '${containerName}': does not exist")
        }
        JSONObject container
        try {
            container = this.steps.readJSON text: containerString
        } catch (Exception ex) { // groovylint-disable-line CatchException
            throw new Exception("Error reading JSON '${containerString}' for container '${containerName}': ${ex.getMessage()}")
        }
        return container
    }

}
