package org.example.rest.word;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rx.Single;

import java.util.UUID;

@Service
public class WordService {

    private Logger logger = LoggerFactory.getLogger(WordService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Single<String> addWord(String word) {
        return Single.create(singleSubscriber -> {
            String key = UUID.randomUUID().toString();
            logger.info("Publishing word with key {}", key);
            kafkaTemplate.send("words", key, word)
                    .addCallback(
                            result -> singleSubscriber.onSuccess(key),
                            throwable -> singleSubscriber.onError(throwable));
        });
    }
}
