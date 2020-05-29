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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.CassandraContainer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OriginRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SentenceRestTest {

    @LocalServerPort
    int port;

    static CassandraContainer cassandra = new CassandraContainer();

    static {
        cassandra.start();
        Cluster cluster = cassandra.getCluster();
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS origin WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};");
            session.execute("CREATE TABLE IF NOT EXISTS origin.sentences ( key varchar,sentence varchar, PRIMARY KEY (key) );");
            session.execute("CREATE CUSTOM INDEX search_index ON origin.sentences (sentence) USING 'org.apache.cassandra.index.sasi.SASIIndex'\n" +
                    "WITH OPTIONS = {\n" +
                    "'mode': 'CONTAINS',\n" +
                    "'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.NonTokenizingAnalyzer',\n" +
                    "'case_sensitive': 'false'};\n");

            session.execute("insert into origin.sentences(key, sentence) values ('0', 'hello');");
            session.execute("insert into origin.sentences(key, sentence) values ('1', 'world');");
        }
    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("cassandra", cassandra::getContainerIpAddress);
        registry.add("port", () -> cassandra.getMappedPort(9042));
    }

    @Test
    public void testGet() throws Exception {
        Response response = given().contentType(ContentType.TEXT)
                .queryParam("sentence", "o")
                .port(port)
                .get("/api/sentences")
                .then()
                .statusCode(200)
                .extract()
                .response();
        List list = response.as(List.class);
        assertThat(list.size()).isEqualTo(2);
    }
}
