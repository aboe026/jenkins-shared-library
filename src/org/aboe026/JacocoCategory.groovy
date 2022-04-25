package org.aboe026

import com.cloudbees.groovy.cps.NonCPS

enum JacocoCategory {

    BRANCH_COVERAGE('branchCoverage'),
    CLASS_COVERAGE('classCoverage'),
    COMPLEXITY_SCORE('complexityScore'),
    INSTRUCTION_COVERAGE('instructionCoverage'),
    LINE_COVERAGE('lineCoverage'),
    METHOD_COVERAGE('methodCoverage')

    final String value

    private JacocoCategory(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}
