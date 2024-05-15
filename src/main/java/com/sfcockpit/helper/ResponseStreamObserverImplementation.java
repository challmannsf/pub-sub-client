package com.sfcockpit.helper;

import com.salesforce.eventbus.protobuf.FetchResponse;

import io.grpc.stub.StreamObserver;

public class ResponseStreamObserverImplementation implements StreamObserver<FetchResponse> {

    @Override
    public void onNext(FetchResponse value) {
        System.out.println(value);
        System.out.println("ResponseStreamObserverImplementation onNext");
    }

    @Override
    public void onError(Throwable t) {
        throw new UnsupportedOperationException("Unimplemented method 'onError'");
    }

    @Override
    public void onCompleted() {
        System.out.println("ResponseStreamObserverImplementation onCompleted");
    }


}
