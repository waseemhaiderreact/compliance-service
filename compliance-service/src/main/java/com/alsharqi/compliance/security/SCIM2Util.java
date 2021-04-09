package com.alsharqi.compliance.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.alsharqi.compliance.util.ApplicationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SCIM2Util {

    private static final Logger LOGGER = LogManager.getLogger(SCIM2Util.class);

    @Value("${scimMeEndpoint}")
    private String meEndpoint;


    // Returns externalID(User name) of current context(logged in) User
    public String getCurrentUser(String authHeader){
        HttpClient getCurrentUser = null;
        GetMethod getMethod = null;
        int responseCode = 0;
        String response = null;

        try{
            getCurrentUser = new HttpClient();
            getMethod = new GetMethod(meEndpoint);
            getMethod.addRequestHeader("Authorization", authHeader);
            responseCode = getCurrentUser.executeMethod(getMethod);
            response = getMethod.getResponseBodyAsString();

            if(responseCode != 200)
                throw new ApplicationException("AS-005","Failed to retrieve Identity Server User against header: "+authHeader);

            if(!new JSONObject(response).keySet().contains("externalId"))
                throw new ApplicationException("As-005","ExternalID of User "+new JSONObject(response).getString("userName")+" is not present");


        }catch(ApplicationException ae){
            LOGGER.error("A Known Error occurrent while obtaining Identity Server user details, responseCode: "+responseCode+" , response: "+response,ae);
            ae = null;
        }catch(Exception e){
            LOGGER.error("An Unknown Error occurred while obtaining Identity Server User against Auth Header: "+authHeader,e);
            e = null;
        }finally{
            getCurrentUser = null;
            getMethod = null;
            responseCode = 0;
        }


        return new JSONObject(response).getString("externalId");

    }

    public String convertToJSON (Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper.writeValueAsString(obj);
    }
}
