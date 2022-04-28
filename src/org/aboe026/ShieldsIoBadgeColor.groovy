package org.aboe026

import com.cloudbees.groovy.cps.NonCPS

enum ShieldsIoBadgeColor {

    BRIGHT_GREEN('brightgreen'),
    GREEN('green'),
    YELLOW_GREEN('yellowgreen'),
    YELLOW('yellow'),
    ORANGE('orange'),
    RED('red'),
    BLUE('blue'),
    LIGHT_GREY('lightgrey')

    final String value

    private ShieldsIoBadgeColor(String value) {
        this.value = value
    }

    @Override
    @NonCPS
    String toString() {
        return this.value
    }

}
