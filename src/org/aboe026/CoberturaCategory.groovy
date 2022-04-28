package org.aboe026

import com.cloudbees.groovy.cps.NonCPS

enum CoberturaCategory {

    CLASSES('Classes'),
    CONDITIONALS('Conditionals'),
    FILES('Files'),
    LINES('Lines'),
    METHODS('Methods'),
    PACKAGES('Packages')

    final String value

    private CoberturaCategory(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}
