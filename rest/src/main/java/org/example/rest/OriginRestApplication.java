package org.example.rest;

import com.datastax.driver.core.Cluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OriginRestApplication {

    private @Value("${cassandra}")
    String cassandra;

    private @Value("${port}")
    int port;

    public static void main(String[] args) {
        SpringApplication.run(OriginRestApplication.class, args);
    }

    @Bean
    Cluster cluster() {
        return Cluster.builder().addContactPoint(cassandra).withPort(port).build();
    }
}
