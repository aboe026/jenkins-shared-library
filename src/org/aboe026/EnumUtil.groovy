package org.aboe026

/** Utility to help with Enums.
  */
class EnumUtil {

    static List<String> getValues(Class<? extends Enum<?>> enumeration) {
        // groovylint-disable-next-line UnnecessaryCollectCall
        return enumeration.values().collect { Enum value ->
            value.toString()
        }
    }

}
