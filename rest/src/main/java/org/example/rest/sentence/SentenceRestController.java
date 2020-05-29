package org.example.rest.sentence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.List;

@RestController
@RequestMapping(value = "/api/sentences")
public class SentenceRestController {

    @Autowired
    private SentenceService sentenceService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<List<Sentence>>> get(@RequestParam String sentence) {
        return sentenceService.searchSentence(sentence).subscribeOn(Schedulers.io()).map(key -> ResponseEntity.ok(key));
    }
}
