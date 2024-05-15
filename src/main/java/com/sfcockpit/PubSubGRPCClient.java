package com.sfcockpit;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import com.salesforce.eventbus.protobuf.*;
import com.sfcockpit.helper.ConfigurationService;
import com.sfcockpit.helper.LoginService;
import com.sfcockpit.helper.ResponseStreamObserverImplementation;
import com.sfcockpit.helper.LoginService.LoginServiceException;

public class PubSubGRPCClient {

    private final PubSubGrpc.PubSubBlockingStub pubSubBlockingStub;
    private final PubSubGrpc.PubSubStub pubSubAsyncStub; 
    private final ManagedChannel channel;

    private final StreamObserver<FetchResponse> responseStreamObserver;

    private final String pubSubHost = "api.pubsub.salesforce.com";
    private final int pubSubPort = 7443;

    public PubSubGRPCClient() throws IOException, InterruptedException, LoginServiceException {
        this.channel = ManagedChannelBuilder.forAddress(this.pubSubHost, this.pubSubPort).build();

        // TODO
        ConfigurationService config = new ConfigurationService("exampleConfiguration.yaml");
        CallCredentials callCredentials = new LoginService(config).login();
        this.pubSubAsyncStub = PubSubGrpc.newStub(channel).withCallCredentials(callCredentials);
        this.pubSubBlockingStub = PubSubGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
        this.responseStreamObserver = new ResponseStreamObserverImplementation();

        // TEST 
        this.setTopicInfo();
        this.subscribe();

    }

    public void waitInMillis(long duration) {
        synchronized (this) {
            try {
                this.wait(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Subscribe and listen to a specified topic
     */
    public void subscribe() {

        StreamObserver<FetchRequest> serverStream = this.pubSubAsyncStub.subscribe(this.responseStreamObserver);
        FetchRequest.Builder fetchRequestBuilder = FetchRequest.newBuilder()
                .setNumRequested(10)
                .setTopicName("/event/TestEvent__e")
                .setReplayPreset(ReplayPreset.EARLIEST); // ReplayPreset.EARLIEST = -2  otherwise ReplayPreset.LATEST
        serverStream.onNext(fetchRequestBuilder.build());

        System.out.println("YOU HAVE X SECONDS ");
        waitInMillis(10000);
    }

    public void setTopicInfo() {
        TopicInfo topicInfo = this.pubSubBlockingStub.getTopic(TopicRequest.newBuilder().setTopicName("/event/TestEvent__e").build());
        topicInfo.getAllFields().entrySet().forEach(item -> {
       //     System.out.println(item.getValue());
        });
    }
}
