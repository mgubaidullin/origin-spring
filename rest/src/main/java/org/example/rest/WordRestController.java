package org.example.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Single;
import rx.schedulers.Schedulers;

@RestController
@RequestMapping(value = "/api/word")
public class WordRestController {

    @Autowired
    private WordService wordService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Single<ResponseEntity<String>> execute(@RequestBody final String word) {
        return wordService.addWord(word).subscribeOn(Schedulers.io()).map(key -> ResponseEntity.ok(key));
    }
}
