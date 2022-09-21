package com.alsharqi.compliance.util;

//import com.alsharqi.authentication.security.CustomAuthority;
//import com.alsharqi.authentication.security.User;

import org.apache.logging.log4j.ThreadContext;
import org.json.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import java.util.LinkedHashMap;

/*
*    Description: All util functions, commonly used throughout this service are included in this class. Ideally, this class should be in commons
*    Version History: v1
*
*    Version        Author            Description                 Date
*    ==================================================================
*    v1            Ali Javed     Initial Version            28-Oct-2019
@
*/

public class Util {

    /*
    * We need to use session ID and logged in user name in log4j's default logging pattern. This function setts these values in the ThreadContext.
    * */



//    public void setThreadContextForLogging() {
//        // TODO: Implement Zuul and use interceptor
//        try {
//            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
//            Authentication principal = SecurityContextHolder.getContext().getAuthentication();
//            if (principal != null) {
//                if(principal.getPrincipal() instanceof LinkedHashMap){
//                    String userName = ((LinkedHashMap) principal.getPrincipal()).get("username")+"";
//                    ThreadContext.put("userId", userName);
//                }
//                else if(principal.getPrincipal() instanceof User){
//                    String userName = ((User) principal.getPrincipal()).getUsername()+"";
//                    ThreadContext.put("userId", userName);
//                }
//
//
//            }
//            ThreadContext.put("sessionId", sessionId);
//        } catch (Exception e) {
//            // Unable to set thread context. Since it is a non-blocking error, the normal execution should still continue to work.
//            e.printStackTrace();
//        }
//
//    }

    public void setThreadContextForLogging() {
        // TODO: Implement Zuul and use interceptor
        try {
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

            String userName = Util.getJWTUsername();

            ThreadContext.put("userId", userName);
            ThreadContext.put("sessionId", sessionId);

        } catch (Exception e) {
            // Unable to set thread context. Since it is a non-blocking error, the normal execution should still continue to work.
            e.printStackTrace();
        }

    }

    /*
    * Clear ThreadContext values set in setThreadContextForLogging function. Below funciton should only be called when setThreadContextForLogging is invoked before it.
    * */
    public void clearThreadContextForLogging() {
        ThreadContext.clearMap();

    }

    public static boolean containsAnyWord(String inputString, String[] items) {
        boolean found = false;
        return Arrays.stream(items).anyMatch(role -> inputString.contains(role));
    }

    public static boolean containsPrivilege(List<String> principalStringAuthorities, String[] items) {

//        List<String> principalStringAuthorities = new ArrayList<>();
//        authorities.forEach(x -> principalStringAuthorities.add(x.getAuthority()));

        return Arrays.stream(items).anyMatch(item -> principalStringAuthorities.contains(item));
    }

    // Returns a username extracted from JWT email Claim.
    public static String getJWTUsername(){
        String authHeader = ((String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization")).split(" ")[1].split("\\.")[1];
        return ((String) (new JSONObject(new String(Base64.getDecoder().decode(authHeader)))).get("email"));
    }

    // Returns an Array of roles extracted from JWT groups Claim.
    public static List<Object> getJWTRoles(){
        String authHeader = ((String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization")).split(" ")[1].split("\\.")[1];
        return (new JSONObject(new String(Base64.getDecoder().decode(authHeader)))).getJSONArray("groups").toList();
    }

    // Returns a uuid extracted from JWT subject Claim.
    public static Long getJWTUuid(){
        String authHeader = ((String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization")).split(" ")[1].split("\\.")[1];
        return Long.parseLong(((String) (new JSONObject(new String(Base64.getDecoder().decode(authHeader)))).get("sub")));
    }



}
