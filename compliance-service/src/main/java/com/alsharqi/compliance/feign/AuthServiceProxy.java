package com.alsharqi.compliance.feign;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name="auth-service",url="${auth-service.url}")
public interface AuthServiceProxy {

    @RequestMapping(method = RequestMethod.GET,value="/user")
    public Map<String,Object> getUserDetails(@RequestHeader("Authorization") String bearerToken);

}
