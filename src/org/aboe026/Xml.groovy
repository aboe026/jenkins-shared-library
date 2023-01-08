package org.aboe026

import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

/** Utility for XML files
  */
class Xml {

    /* Can be called in Jenkinsfile like:
     *
     *     @Library('aboe026') _ // groovylint-disable-line VariableName, UnusedVariable
     *
     *     import org.aboe026.Xml
     *
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
     *     writeFile (
     *         file: filePath,
     *         text: Xml.transform(readFile(file: filePath)) { root ->
     *             root.each { testsuite ->
     *                 testsuite.testcase.each { testcase ->
     *                     def unstable = false
     *                     def testcaseClass = testcase['@classname'].text()
     *                     testcase['@classname'] = "${suiteName}.${testcaseClass}".toString()
     *                     def testcaseName = testcase['@name'].text()
     *                     if (testcaseName.contains('(unstable)')) {
     *                         unstable = true
     *                     }
     *                     testcaseName = testcaseName
     *                         .replaceAll(/ \(unstable\)/, '')
     *                         .replaceAll(/ \(screenshots: \S+\)/, '')
     *                     if (unstable) {
     *                         unstableTests.push("${testcaseClass}.${testcaseName}")
     *                     }
     *                     testcase['@name'] = testcaseName
     *                 }
     *             }
     *         }
     *     }
     */
    static String transform(String text, Closure transformation) {
        GPathResult xml
        try {
            xml = new XmlSlurper().parseText(text)
        } catch (Exception ex) { // groovylint-disable-line CatchException
            throw new Exception("Error parsing '${text}' as XML: ${ex.getMessage()}")
        }
        transformation(xml) // pass by reference will update xml
        return new XmlUtil().serialize(xml)
    }

}
