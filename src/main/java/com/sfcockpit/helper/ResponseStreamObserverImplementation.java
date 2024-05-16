package com.sfcockpit.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;

import com.salesforce.eventbus.protobuf.ConsumerEvent;
import com.salesforce.eventbus.protobuf.FetchResponse;
import com.salesforce.eventbus.protobuf.PubSubGrpc;
import com.salesforce.eventbus.protobuf.SchemaRequest;

import io.grpc.stub.StreamObserver;

public class ResponseStreamObserverImplementation implements StreamObserver<FetchResponse> {

    private final PubSubGrpc.PubSubBlockingStub pubSubBlockingStub;

    public ResponseStreamObserverImplementation(PubSubGrpc.PubSubBlockingStub pubSubBlockingStub) {
        this.pubSubBlockingStub = pubSubBlockingStub;
    }

    @Override
    public void onNext(FetchResponse value) {
        try {
            this.encodeResponse(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable t) {
        throw new UnsupportedOperationException("Unimplemented method 'onError'");
    }

    @Override
    public void onCompleted() {
        System.out.println("ResponseStreamObserverImplementation onCompleted");
    }

    /**
     * Please refer to https://avro.apache.org/docs/1.11.1/getting-started-java/#serializing-and-deserializing-without-code-generation
     * for implementation details.
     * We are parsing the schem on the fly
     * @param value
     * @throws IOException 
     */
    private void encodeResponse(FetchResponse value) throws IOException {

        for(ConsumerEvent consumerEvent : value.getEventsList()) {
            // Get the schema
            SchemaRequest request = SchemaRequest.newBuilder().setSchemaId(consumerEvent.getEvent().getSchemaId()).build();
            String schemaJson = this.pubSubBlockingStub.getSchema(request).getSchemaJson();
            Schema currentSchema = new Schema.Parser().parse(schemaJson);

            // Perform deserialize
            DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(currentSchema);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(consumerEvent.getEvent().getPayload().toByteArray());
            BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(byteArrayInputStream, null);
            GenericRecord record =  reader.read(null, decoder);
            System.out.println(record.toString());
        }
    } 


}
