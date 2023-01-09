package org.aboe026

/** Class for Operating System idiosyncrasies
  */
class OsUtil {

    static String getLineEnding() {
        return System.properties['os.name'].toLowerCase().contains('windows')
        ?
            '\r\n'
        :
            '\n'
    }

}
