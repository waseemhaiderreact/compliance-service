package com.alsharqi.compliance.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component

public class RequestInterceptor extends HandlerInterceptorAdapter {

    private final Logger LOGGER = LogManager.getLogger(RequestInterceptor.class);

    @Value("${introspectionEndpoint}")
    private String introspectionEndpoint;

    private IntrospectionHandler introspectionHandler;

    @PostConstruct
    void initIntrospectionHandler(){
        this.introspectionHandler = new IntrospectionHandler(introspectionEndpoint,true);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String bearerToken = request.getHeader("Authorization");

        try{

//            if(this.introspectionHandler == null)
//                this.introspectionHandler = new IntrospectionHandler(introspectionEndpoint,true);

            // Validate for authorization
            if (!this.introspectionHandler.isAuthorized(bearerToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Sorry.Invalid/Revoked/Expired Access Token.");
                return false;
            }

        }catch(Exception e){
            LOGGER.error("An Error occurred while Validating Token "+bearerToken,e);
            e = null;
            return false;
        }finally{
            bearerToken = null;
        }

        return true;
    }


}
