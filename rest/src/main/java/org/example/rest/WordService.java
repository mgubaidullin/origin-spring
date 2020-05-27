package org.example.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rx.Single;

import java.util.UUID;

@Service
public class WordService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Single<String> addWord(String word) {
        return Single.create(singleSubscriber -> {
            String key = UUID.randomUUID().toString();
            kafkaTemplate.send("words", key, word);
            singleSubscriber.onSuccess(key);
        });
    }
}
