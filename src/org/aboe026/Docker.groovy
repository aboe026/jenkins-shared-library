import net.sf.json.JSONObject

/** Utility for interacting with Docker
  */
class Docker implements Serializable {

    private static final long serialVersionUID = 1L
    private final Script steps

    // Not allowed, need script steps for method executions
    Docker() {
        throw new Exception('Empty constructor not valid for Docker class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).')
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.Docker
     *
     *      def docker = new Docker(this)
     */
    Docker(Script steps) {
        if (!steps) {
            throw new Exception("Invalid first parameter \"${steps}\" passed to \"Docker\" constructor: Must be non-null Script object.")
        }
        this.steps = steps
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.Docker
     *
     *      def docker = new Docker(
     *          steps: this
     *      )
     */
    Docker(Map params) {
        ParameterValidator.required(params, 'Docker', 'steps', true)
        this.steps = params.steps
    }

    /* Can be called in Jenkinsfile like:
     *
     *     def workDir = "${WORKSPACE}/${env.BRANCH_NAME}-${env.BUILD_ID}"
     *     def mountDir = docker.getHostMountDir(workDir)
     */
    String getHostMountDir(String workDir, String jenkinsContainerName = 'cicd-jenkins-1', String mountDestination = '/var/jenkins_home') {
        String mountSource = ''
        String containerString = this.steps.sh(
            script: "docker inspect ${jenkinsContainerName}",
            returnStdout: true
        )
        JSONObject container = this.steps.readJSON text: containerString
        container[0].Mounts.each { mount ->
            if (mount.Destination == mountDestination) {
                mountSource = mount.Source.replaceAll('\\\\', '\\\\\\\\')
            }
        }
        return workDir.replace(mountDestination, mountSource)
    }

    /* Can be called in Jenkinsfile like:
     *
     *     def dockerVolumesToDeleteCsv = docker.getContainerVolumes("${uniqueName}-database-1")
     */
    String getContainerVolumes(String containerName) {
        String containerString = this.steps.sh(
            script: "docker inspect $containerName",
            returnStdout: true
        )
        JSONObject container = this.steps.readJSON text: containerString
        String[] volumeNames = []
        container[0].Mounts.each { mount ->
            if (mount.Type == 'volume') {
                volumeNames.push(mount.Name)
            }
        }
        return volumeNames.join(',')
    }

}
