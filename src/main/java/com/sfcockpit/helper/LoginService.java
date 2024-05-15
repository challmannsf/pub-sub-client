package com.sfcockpit.helper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

import io.grpc.CallCredentials;


public class LoginService  {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginSuccessResponse {
        public String access_token;
        public String signature;
        public String instance_url;
        public String id;
        public String issued_at;
    }

    ConfigurationService currentConfiguration; 
    public LoginService(ConfigurationService configurationService) {
       this.currentConfiguration = configurationService;
    }

    public class LoginServiceException extends Exception {
        public LoginServiceException(String string) {
            super(string);
        }
    }

    public CallCredentials login() throws IOException, InterruptedException, LoginServiceException {
   
        LoginService.LoginSuccessResponse response = this.oauthClientCredentialsFlow();
        
        String accessToken = response.access_token;
        String instanceURL = response.instance_url;
        String tenantId = extractOrgId(response.id);
        return new SalesforceCallCredentials(instanceURL, accessToken, tenantId);
    }

    /**
     * Currently we only support that specific flow for authentication
     * @throws InterruptedException 
     */
    private LoginService.LoginSuccessResponse oauthClientCredentialsFlow() throws IOException, InterruptedException {
        String url = "https://" +  this.currentConfiguration.myDomainUrl + "/services/oauth2/token";
        
        Map<String, String> formDataMap = new HashMap<String, String>();
        formDataMap.put("grant_type", "client_credentials");
        formDataMap.put("client_id", this.currentConfiguration.clientId);
        formDataMap.put("client_secret", this.currentConfiguration.clientSecret);
        String formBodyAsString = this.buildMapToParamString(formDataMap);

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(BodyPublishers.ofString(formBodyAsString))  
        .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        if (response.statusCode() != 200) {
            throw new InterruptedException("API error: " + response.body());
        }        

        // parse JSON with jackson
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        LoginService.LoginSuccessResponse loginSuccessResponse = mapper.readValue(response.body(), LoginService.LoginSuccessResponse.class);

        return loginSuccessResponse;
    }


    private String buildMapToParamString(Map<String, String> map) {
        String param = "";
        for(String key : map.keySet()) {
            param = param + key + "=" + map.get(key) + "&";
        }
        // TODO - remove last "&" from param 
        return param;
    }

    /**
     * Fetches the Org Id from a standard oauth response which is in the following pattern: 
     * https://login.salesforce.com/id/<ORGID>/<USERID>
     * @param instanceId
     * @return orgId
     * @throws LoginServiceException 
     */
    private String extractOrgId(String instanceId) throws LoginServiceException {
        String[] parts = instanceId.split("id/");
        if (parts.length >= 2) {
            return parts[1].split("/")[0];
        } else {
            throw new LoginServiceException("Can't fetch orgId from " + instanceId);
        }
    }

     
}
