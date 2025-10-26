package com.example.kafkaappconsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WordCounter {

    @Value("${app.stop-words}.split(',').toLowerCase()")
    private Set<String> stopWords = new HashSet<>(Arrays.asList("the", "to", "a", "and", "is", "are", "or", "in", "at", "on", "me", "i", "we", "you", "he", "she", "it"));

    public Stream<String> splitText(String text) {
        return Arrays.stream(text.replaceAll("\\p{P}", "").toLowerCase().split("\\s"));
    }

    public Stream<WordFrequency> groupByWords(Stream<String> words) {
        return words
                .filter(this::isNotStopWord)
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                .entrySet()
                .stream().map(e -> new WordFrequency(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(WordFrequency::count).reversed());
    }

    private boolean isNotStopWord(String word) {
        return !stopWords.contains(word);
    }
}