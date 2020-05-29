package org.example.storage;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageRoute extends EndpointRouteBuilder {

    private @Value("${cassandra}")
    String cassandra;

    private final String CQL = "insert into sentences(key, sentence) values (?, ?)";

    public void configure() throws Exception {

        errorHandler(deadLetterChannel(kafka("dead-letter-queue").getUri())
                .useOriginalMessage().maximumRedeliveries(5).redeliveryDelay(5000));

        from(kafka("sentences"))
                .log("Storing sentence with key ${headers.kafka.KEY}")
                .setBody(constant(List.of(header("kafka.KEY"), body())))
                .toD(cql(cassandra + "/origin?cql=" + CQL));
    }
}
