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

    private final PubSubGrpc.PubSubStub pubSubAsyncStub; 
    private final PubSubGrpc.PubSubBlockingStub pubSubBlockingStub; 
    private final ManagedChannel channel;
    private final StreamObserver<FetchResponse> responseStreamObserver;
    private final String pubSubHost = "api.pubsub.salesforce.com";
    private final int pubSubPort = 7443;

    private Boolean keepAlive = true;

    public PubSubGRPCClient(String configurationFileName) throws IOException, InterruptedException, LoginServiceException {
        this.channel = ManagedChannelBuilder.forAddress(this.pubSubHost, this.pubSubPort).build();
        ConfigurationService config = new ConfigurationService(configurationFileName);
        CallCredentials callCredentials = new LoginService(config).login();
        this.pubSubAsyncStub = PubSubGrpc.newStub(channel).withCallCredentials(callCredentials);
        this.pubSubBlockingStub = PubSubGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
        this.responseStreamObserver = new ResponseStreamObserverImplementation(this.pubSubBlockingStub);    
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
    public void subscribe(String eventName) {

        StreamObserver<FetchRequest> serverStream = this.pubSubAsyncStub.subscribe(this.responseStreamObserver);
        this.listenToShutdown();
        while(this.keepAlive) {
            waitInMillis(1000);
            FetchRequest.Builder fetchRequestBuilder = FetchRequest.newBuilder()
            .setNumRequested(10)
            .setTopicName(eventName)
            .setReplayPreset(ReplayPreset.EARLIEST); // ReplayPreset.EARLIEST = -2  otherwise ReplayPreset.LATEST
            serverStream.onNext(fetchRequestBuilder.build());
        }
    }

    public void listenToShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              System.err.println("shutting down GRPC Client");
              PubSubGRPCClient.this.keepAlive = false;
            }
        });
    }

}
