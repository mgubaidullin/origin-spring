package org.example.rest.word;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Single;
import rx.schedulers.Schedulers;

@RestController
@RequestMapping(value = "/api/words")
public class WordRestController {

    @Autowired
    private WordService wordService;

    @PostMapping(value = "", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<String>> post(@RequestBody final String word) {
        return wordService.addWord(word).subscribeOn(Schedulers.io()).map(key -> ResponseEntity.ok(JSONObject.quote(key)));
    }
}
