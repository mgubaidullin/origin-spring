package org.example.rest.sentence;


import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("sentences")
public class Sentence {
    @PrimaryKey
    private final String key;
    private final String sentence;

    public Sentence(String key, String sentence) {
        this.key = key;
        this.sentence = sentence;
    }

    public String getKey() {
        return key;
    }

    public String getSentence() {
        return sentence;
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "key='" + key + '\'' +
                ", sentence='" + sentence + '\'' +
                '}';
    }
}
