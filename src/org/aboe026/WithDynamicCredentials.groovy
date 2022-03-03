package org.aboe026

/** Manage Jenkins dynamic Credentials provisioning
  */
class WithDynamicCredentials {

    static void fromString(Script steps, String credentialsId, Closure nestedSteps) {
        String guid = UUID.randomUUID()
        String envVarName = "aboe026_Dynamic_Credentials_ID_${guid}"
        String secretVariableName = "aboe026_Dynamic_Secret_Variable_Name_${guid}"
        steps.withEnv([
            "${envVarName}=${credentialsId}" // need to define this as environment variable to be able to have credentialsId be dynamic below
        ]) {
            steps.withCredentials([
                steps.string(credentialsId: steps.env[envVarName], variable: secretVariableName)
            ]) {
                nestedSteps(secretVariableName)
            }
        }
    }

    static void fromUsernamePassword(Script steps, String credentialsId, Closure nestedSteps) {
        String guid = UUID.randomUUID()
        String envVarName = "aboe026_Dynamic_Credentials_ID_${guid}"
        String secretUsernameVariableName = "aboe026_Dynamic_Secret_Variable_Name_${guid}_user"
        String secretPasswordVariableName = "aboe026_Dynamic_Secret_Variable_Name_${guid}_password"
        steps.withEnv([
            "${envVarName}=${credentialsId}" // need to define this as environment variable to be able to have credentialsId be dynamic below
        ]) {
            steps.withCredentials([
                steps.usernamePassword(credentialsId: steps.env[envVarName], usernameVariable: secretUsernameVariableName, passwordVariable: secretPasswordVariableName)
            ]) {
                nestedSteps(secretUsernameVariableName, secretPasswordVariableName)
            }
        }
    }

}
