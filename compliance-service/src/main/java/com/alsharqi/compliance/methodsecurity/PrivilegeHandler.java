package com.alsharqi.compliance.methodsecurity;

import com.alsharqi.compliance.security.Authorities;
import com.alsharqi.compliance.util.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("PrivilegeHandler")
public class PrivilegeHandler {
    public boolean hasCompliancePrivilege(String specificPrivilege){
        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        if(principal==null){
            return false;
        }
        String[] authoritiesToCheck = new String[]{
                specificPrivilege,
                Authorities.Compliance.FULL,
                Authorities.ROLE_ADMIN
        };
        return Util.containsAnyWord(principal.getAuthorities().toString(), authoritiesToCheck);
    }
}
