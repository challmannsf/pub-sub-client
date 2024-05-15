package com.sfcockpit.helper;

import java.util.concurrent.Executor;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

public class SalesforceCallCredentials extends CallCredentials {


    private String instanceURL;
    private String accessToken;
    private String tenantId;
    
    public SalesforceCallCredentials(String instanceURL, String accessToken, String tenantId) {
        this.instanceURL = instanceURL;
        this.accessToken = accessToken;
        this.tenantId = tenantId;
    }


    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
        Metadata headers = new Metadata();
        Metadata.Key<String> instanceURLKey = Metadata.Key.of("instanceUrl", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> accessTokenKey = Metadata.Key.of("accessToken", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> tenantId = Metadata.Key.of("tenantId", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(instanceURLKey, this.instanceURL);
        headers.put(accessTokenKey, this.accessToken);
        headers.put(tenantId, this.tenantId);
        applier.apply(headers);
        System.out.println("calling applyRequestMetadata");
    }

}
