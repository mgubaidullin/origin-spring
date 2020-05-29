package org.example.rest.sentence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig {

    private @Value("${cassandra}")
    String cassandra;
    private @Value("${port}")
    int port;

    @Bean
    public CqlSessionFactoryBean session() {

        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints(cassandra);
        session.setPort(port);
        session.setKeyspaceName("origin");
        session.setLocalDatacenter("datacenter1");
        return session;
    }
}