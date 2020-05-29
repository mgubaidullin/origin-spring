package org.example.rest;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OriginRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestTest {

    AtomicReference<String> result = new AtomicReference();

    @LocalServerPort
    int port;

    static KafkaContainer kafka = new KafkaContainer();
    static CassandraContainer cassandra = new CassandraContainer();

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("bootstrap.address", kafka::getBootstrapServers);
        registry.add("cassandra", cassandra::getContainerIpAddress);
    }

    static {
        kafka.start();
        System.setProperty("bootstrap.address", kafka.getBootstrapServers());
        System.setProperty("cassandra", cassandra.getContainerIpAddress());

        cassandra.start();
        System.setProperty("port", cassandra.getMappedPort(9042).toString());
        Cluster cluster = cassandra.getCluster();
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS origin WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};");
            session.execute("CREATE TABLE IF NOT EXISTS origin.sentences ( key varchar,sentence varchar, PRIMARY KEY (key) );");
            session.execute("CREATE CUSTOM INDEX search_index ON origin.sentences (sentence) USING 'org.apache.cassandra.index.sasi.SASIIndex'\n" +
                    "WITH OPTIONS = {\n" +
                    "'mode': 'CONTAINS',\n" +
                    "'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.NonTokenizingAnalyzer',\n" +
                    "'case_sensitive': 'false'};\n");
        }
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
