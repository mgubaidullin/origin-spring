package org.example.rest.sentence;

import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;
import rx.Single;

import java.util.List;

@Service
public class SentenceService {

    private Logger logger = LoggerFactory.getLogger(SentenceService.class);

    @Autowired
    CqlSessionFactoryBean session;

    public Single<List<Sentence>> searchSentence(String sentence) {
        return Single.create(singleSubscriber -> {
            logger.info("Searching sentence contains {}", sentence);
            Try.of(() -> {
                CassandraOperations template = new CassandraTemplate(session.getObject());
                return template.select(Query.query(Criteria.where("sentence").like("%" +sentence + "%")), Sentence.class);
            })
                    .onSuccess(t -> singleSubscriber.onSuccess(t))
                    .onFailure(throwable -> singleSubscriber.onError(throwable));
        });
    }
}
