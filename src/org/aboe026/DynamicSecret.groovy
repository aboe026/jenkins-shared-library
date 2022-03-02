package org.aboe026

/** Manage Jenkins Secret provisioning
  */
class DynamicSecret {

    static void asVariable(Script steps, String credentialsId, Closure nestedSteps) {
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

}
