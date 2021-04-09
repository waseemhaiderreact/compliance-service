package com.alsharqi.compliance.methodsecurity;

import com.alsharqi.compliance.feign.AuthServiceProxy;
import com.alsharqi.compliance.security.Authorities;
import com.alsharqi.compliance.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Component("PrivilegeHandler")
public class PrivilegeHandler {

    @Autowired
    private AuthServiceProxy authServiceProxy;

    public boolean hasCompliancePrivilege(String specificPrivilege){
//        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        Map<String,Object> principal = authServiceProxy.getUserDetails(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization"));
        if(principal==null){
            return false;
        }
        String[] authoritiesToCheck = new String[]{
                specificPrivilege,
                Authorities.Compliance.FULL,
                Authorities.ROLE_ADMIN
        };
        return Util.containsAnyWord(principal.get("authorities").toString(), authoritiesToCheck);
    }
}
