package com.alsharqi.compliance.util;

import com.alsharqi.compliance.security.SCIM2Util;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Util {
    /*
     * Clear ThreadContext values set in setThreadContextForLogging function. Below funciton should only be called when setThreadContextForLogging is invoked before it.
     * */

    public void setThreadContextForLogging(SCIM2Util scim2Util) {
        // TODO: Implement Zuul and use interceptor
        try {
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

            String userName = scim2Util.getCurrentUser(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization"));

            ThreadContext.put("userId", userName);
            ThreadContext.put("sessionId", sessionId);

        } catch (Exception e) {
            // Unable to set thread context. Since it is a non-blocking error, the normal execution should still continue to work.
            e.printStackTrace();
        }

    }

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
