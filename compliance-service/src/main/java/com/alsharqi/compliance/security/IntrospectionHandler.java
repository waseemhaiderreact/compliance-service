package com.alsharqi.compliance.security;
/* Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntrospectionHandler {

  private final Logger logger = LogManager.getLogger(IntrospectionHandler.class);

  private final String introspectionEndpoint;
  private final boolean introspectionEnabled;

  public IntrospectionHandler(
          final String introspectionEndpoint,
          final boolean introspectionEnabled) {

    this.introspectionEndpoint = introspectionEndpoint;
    this.introspectionEnabled = introspectionEnabled;
  }

  public boolean isAuthorized(final String authHeader) {

    // Ignore introspection if this is not enabled
    if (!introspectionEnabled) {
      return true;
    }

    // Verify for correct auth header : Bearer access_token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return false;
    }

    String[] splits = authHeader.split(" ");

    if (splits.length != 2) {
      return false;
    }

    final JSONObject introspectionResponse = getIntrospectionResponse(splits[1]);

    try {
      return introspectionResponse.getBoolean("active");
    } catch (JSONException e) {
      logger.error("Error while reading introspection response.", e);
      return false;
    }
  }

  private JSONObject getIntrospectionResponse(final String bearerToken) {

    try {
      final URL url = new URL(this.introspectionEndpoint);
      final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

      urlConnection.setRequestMethod("POST");
      urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      urlConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);

      urlConnection.setDoOutput(true);


      final String message = "token=" + bearerToken;

      final OutputStream outputStream = urlConnection.getOutputStream();
      outputStream.write(message.getBytes());
      outputStream.close();

      urlConnection.connect();


      InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      final StringBuilder responseBuilder = new StringBuilder();

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        responseBuilder.append(line);
      }

      return new JSONObject(responseBuilder.toString());
    } catch (IOException e) {
      logger.error("Error while calling token introspection endpoint", e);
      throw new RuntimeException(e);
    }

  }

}