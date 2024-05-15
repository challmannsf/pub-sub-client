package com.sfcockpit.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;


/**
 * Generic configuration which are loaded from a file
 */
public class ConfigurationService {

    String myDomainUrl;
    String clientId;
    String clientSecret;

    public ConfigurationService(String configFileName)  throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream("src/main/resources/" + configFileName);
        HashMap<String, Object> obj = yaml.load(inputStream);
        this.myDomainUrl = obj.get("MY_DOMAIN_URL").toString();
        this.clientId = obj.get("CLIENT_ID").toString();
        this.clientSecret = obj.get("CLIENT_SECRET").toString();
    }

    public String getMyDomainUrl() {
        return this.myDomainUrl;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }
}
