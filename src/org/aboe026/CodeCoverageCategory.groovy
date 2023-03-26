package org.aboe026

import com.cloudbees.groovy.cps.NonCPS

enum CodeCoverageCategory {

    BRANCH('branch'),
    CLASS('class'),
    FILE('file'),
    INSTRUCTION('instruction'),
    LINE('line'),
    METHOD('method'),
    MODULE('module'),
    PACKAGE('package')

    final String value

    private CodeCoverageCategory(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}
