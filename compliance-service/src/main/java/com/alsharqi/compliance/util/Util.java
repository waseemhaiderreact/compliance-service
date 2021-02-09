package com.alsharqi.compliance.util;

import org.apache.logging.log4j.ThreadContext;

public class Util {
    /*
     * Clear ThreadContext values set in setThreadContextForLogging function. Below funciton should only be called when setThreadContextForLogging is invoked before it.
     * */
    public void clearThreadContextForLogging() {
        ThreadContext.clearMap();
    }

    public static boolean containsAnyWord(String inputString, String[] items) {
        boolean found = false;
        for (String item : items) {
            if (inputString.contains(item)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
