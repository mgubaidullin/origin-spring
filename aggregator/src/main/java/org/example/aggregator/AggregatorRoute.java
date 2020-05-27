package org.example.aggregator;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AggregatorRoute extends EndpointRouteBuilder {

    private @Value("${interval}")
    int interval;

    public void configure() throws Exception {
        from(kafka("words"))
                .aggregate(constant("all"), (oldExchange, newExchange) -> {
                    if (oldExchange == null) {
                        return newExchange;
                    }
                    StringBuilder sentence = new StringBuilder(oldExchange.getIn().getBody(String.class))
                            .append(" ").append(newExchange.getIn().getBody(String.class));
                    newExchange.getIn().setBody(sentence.toString());
                    return newExchange;
                })
                .completionInterval(interval)
                .to(kafka("sentences"));
    }
}
