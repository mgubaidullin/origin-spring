package org.example.storage;

import com.datastax.driver.core.Cluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class OriginStorageApplication {

    private @Value("${cassandra}")
    String cassandra;

    public static void main(String[] args) {
        SpringApplication.run(OriginStorageApplication.class, args);
    }

    @Bean
    Cluster cluster() {
        return Cluster.builder().addContactPoint(cassandra).build();
    }
}
