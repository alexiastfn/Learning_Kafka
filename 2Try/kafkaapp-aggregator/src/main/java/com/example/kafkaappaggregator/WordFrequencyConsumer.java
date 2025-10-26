package com.example.kafkaappaggregator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Component
public class WordFrequencyConsumer {

    private static final String TOPIC = "AGGREGATE-DATA";
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private List<WordFrequency> inMemoryFrequencyList = List.of();
    private final AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(topics = TOPIC, groupId = "AGGREGATE_CONSUMERS")
    public void consumeMessage(WordFrequencyList frequencyList) {
        inMemoryFrequencyList = Stream
                .concat(inMemoryFrequencyList.stream(), frequencyList.frequencies().stream())
                .collect(Collectors.groupingBy(WordFrequency::word, Collectors.summingLong(WordFrequency::count))).entrySet()
                .stream()
                .map(e -> new WordFrequency(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(WordFrequency::count).reversed())
                .toList();

        if (counter.incrementAndGet() % 5 == 0) {
            inMemoryFrequencyList.forEach(wordFrequency -> logger.info("In memory word frequency: {}", wordFrequency));
        }
    }
}