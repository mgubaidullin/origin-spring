package org.example.storage;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageRoute extends EndpointRouteBuilder {

    private @Value("${cassandra}")
    String cassandra;

    public void configure() throws Exception {
        String CQL = "insert into sentences(key, sentence) values (?, ?)";

        from(kafka("sentences"))
                .setBody(constant(List.of(header("kafka.KEY"), body())))
                .to(cql(cassandra + "/origin?cql=" + CQL));
    }
}
