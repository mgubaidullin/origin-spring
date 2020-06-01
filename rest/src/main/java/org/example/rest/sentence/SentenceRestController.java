package org.example.rest.sentence;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Single;

import java.util.List;

@RestController
@RequestMapping(value = "/api/sentences")
public class SentenceRestController {

    @Autowired
    private ProducerTemplate producer;


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<List>> get(@RequestParam String sentence) {
        return Single.from(producer.asyncRequestBody("direct:req", sentence, List.class))
                .map(list -> ResponseEntity.ok(list));
    }
}
