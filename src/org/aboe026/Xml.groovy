package org.aboe026

import com.cloudbees.groovy.cps.NonCPS
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

/** Utility for XML files
  */
class Xml {

    private static final long serialVersionUID = 1L
    private final Script steps

    // Not allowed, need script steps for method executions
    Xml() {
        throw new Exception('Empty constructor not valid for Xml class. Must pass script steps (aka "this") as either a parameter (this) or in a Map (steps: this).')
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.Xml
     *
     *      def xml = new Xml(this)
     */
    Xml(Script steps) {
        this.steps = steps
    }

    /** Can be instantiated in a Jenkinsfile like:
     *
     *      @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *      import org.aboe026.Xml
     *
     *      def xml = new Xml(
     *          steps: this
     *      )
     */
    Xml(Map params) {
        ParameterValidator.required(params, 'Xml', 'steps', true)
        this.steps = params.steps
    }

    /* Can be called in Jenkinsfile like:
     *
     *     @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *     import org.aboe026.Xml
     *
     *     def xml = new Xml(this)
     *     def fileName = 'test.xml'
     *     writeFile file: fileName, text: '''<?xml version="1.0" encoding="UTF-8" ?>
     *         |    <testsuite
     *         |        name="TestCafe Tests: Chrome 107.0.0.0 / Alpine Linux 3.17_alpha20220715"
     *         |        tests="4"
     *         |        failures="0"
     *         |        skipped="0"
     *         |        errors="0"
     *         |        time="11.405"
     *         |        timestamp="Sun, 25 Dec 2022 21:35:27 GMT"
     *         |    >
     *         |        <testcase classname="Add" file="/app/test/e2e/build/tests/add.test.js" name="does not refetch on add" time="2.307">
     *         |        </testcase>
     *         |        <testcase classname="Delete" file="/app/test/e2e/build/tests/delete.test.js" name="does not refetch on delete" time="1.296">
     *         |        </testcase>
     *         |        <testcase classname="Edit" file="/app/test/e2e/build/tests/edit.test.js" name="does not refetch on edit" time="2.344">
     *         |        </testcase>
     *         |        <testcase
     *         |            classname="Lifecycle"
     *         |            file="/app/test/e2e/build/tests/lifecycle.test.js"
     *         |            name="add, edit, delete movie (unstable) (screenshots: /path/to/screenhots)"
     *         |            time="3.323"
     *         |        >
     *         |        </testcase>
     *         |        <system-out>
     *         |            <![CDATA[
     *         |                Warnings (1):
     *         |                --
     *         |                The "src", "browsers", and "screenshots.path" options from the configuration file will be ignored.
     *         |            ]]>
     *         |        </system-out>
     *         |    </testsuite>
     *     '''.stripMargin()
     *
     *     def unstableTests = []
     *     xml.transform(fileName) { root ->
     *         root.each { testsuite ->
     *             testsuite.testcase.each { testcase ->
     *                 def unstable = false
     *                 def testcaseClass = testcase['@classname'].text()
     *                 testcase['@classname'] = "${suiteName}.${testcaseClass}".toString()
     *                 def testcaseName = testcase['@name'].text()
     *                 if (testcaseName.contains('(unstable)')) {
     *                     unstable = true
     *                 }
     *                 testcaseName = testcaseName
     *                     .replaceAll(/ \(unstable\)/, '')
     *                     .replaceAll(/ \(screenshots: \S+\)/, '')
     *                 if (unstable) {
     *                     unstableTests.push("${testcaseClass}.${testcaseName}")
     *                 }
     *                 testcase['@name'] = testcaseName
     *             }
     *         }
     *     }
     */
    void transform(String filePath, Closure transformation) {
        String text = this.steps.readFile(file: filePath)
        this.steps.println text
        this.steps.println 'TEST 0'
        GPathResult deserialized = this.deserialize(text)
        this.steps.println 'TEST 1'
        this.steps.println "TEST deserialized: '${deserialized}'"
        transformation(deserialized)
        this.steps.println 'TEST 2'
        String serialized = this.serialize(deserialized)
        this.steps.println 'TEST 3'
        this.steps.println "TEST serialized: '${serialized}'"
        this.steps.println "TEST serialized.getClass(): '${serialized.getClass()}'"
        this.steps.println 'TEST 4'
        // String transformedText = this.performTransformation(text, transformation)
        // this.steps.println transformedText
        this.steps.writeFile(
            file: filePath,
            text: serialized
        )
    }

    @NonCPS
    private GPathResult deserialize(String xml) {
        return new XmlSlurper().parseText(xml)
    }

    @NonCPS
    private String serialize(GPathResult xml) {
        return new XmlUtil().serialize(xml)
    }

    // @NonCPS
    // private String performTransformation(String xmlText, Closure transformation) {
    //     GPathResult xml = new XmlSlurper().parseText(xmlText)
    //     // transformation(xml) // pass by reference will update xml
    //     return new XmlUtil().serialize(xml)
    // }

}
