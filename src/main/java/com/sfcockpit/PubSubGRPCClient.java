package com.sfcockpit;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

import com.salesforce.eventbus.protobuf.*;
import com.sfcockpit.helper.ConfigurationService;
import com.sfcockpit.helper.LoginService;
import com.sfcockpit.helper.LoginService.LoginServiceException;

public class PubSubGRPCClient {

    private final PubSubGrpc.PubSubBlockingStub pubSubBlockingStub;
    private final ManagedChannel channel;

    private final String pubSubHost = "api.pubsub.salesforce.com";
    private final int pubSubPort = 7443;

    public PubSubGRPCClient() throws IOException, InterruptedException, LoginServiceException {
        this.channel = ManagedChannelBuilder.forAddress(this.pubSubHost, this.pubSubPort).build();

        // TODO
        ConfigurationService config = new ConfigurationService("exampleConfiguration.yaml");
        CallCredentials callCredentials = new LoginService(config).login();
        this.pubSubBlockingStub = PubSubGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);

        // TEST 
        this.getTopicInfo();

    }

    public void subscribe() {

    }

    public void getTopicInfo() {
        TopicInfo topicInfo = this.pubSubBlockingStub.getTopic(TopicRequest.newBuilder().setTopicName("/event/TestEvent__e").build());
        topicInfo.getAllFields().entrySet().forEach(item -> {
            System.out.println(item.getValue());
        });
    }
}
