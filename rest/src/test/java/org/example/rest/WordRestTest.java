package org.example.rest;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.KafkaContainer;

import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OriginRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordRestTest {

    AtomicReference<String> result = new AtomicReference();

    @LocalServerPort
    int port;

    static KafkaContainer kafka = new KafkaContainer();
    static CassandraContainer cassandra = new CassandraContainer();

    static {
        kafka.start();
        cassandra.start();
        Cluster cluster = cassandra.getCluster();
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS origin WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("bootstrap.address", kafka::getBootstrapServers);
        registry.add("cassandra", cassandra::getContainerIpAddress);
        registry.add("port", () -> cassandra.getMappedPort(9042));
    }

    @Test
    public void testPost() throws Exception {
        String word = "hello";
        Response response = given().contentType(ContentType.TEXT)
                .body(word)
                .port(port)
                .post("/api/words")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String key = response.asString();
        await().atMost(5, SECONDS).until(() -> result.get() != null);
        assertThat(result.get()).isEqualTo(word);
    }

    @KafkaListener(topics = "words", groupId = "test")
    public void listen(String message) {
        result.set(message);
    }
}
