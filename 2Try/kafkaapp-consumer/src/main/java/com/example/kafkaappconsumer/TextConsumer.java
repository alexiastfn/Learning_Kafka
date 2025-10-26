package com.example.kafkaappconsumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class TextConsumer {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private static final String INPUT_TOPIC = "TEXT-DATA";
    private static final String OUTPUT_TOPIC = "AGGREGATE-DATA";
    private final WordCounter wordCounter;
    private final KafkaTemplate<String, WordFrequencyList> kafkaTemplate;

    public TextConsumer(WordCounter wordCounter, KafkaTemplate<String, WordFrequencyList> kafkaTemplate) {
        this.wordCounter = wordCounter;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void configureTopic(KafkaAdmin kafkaAdmin) {
        kafkaAdmin.createOrModifyTopics(new NewTopic(OUTPUT_TOPIC, 1, (short) 1));
    }

    @KafkaListener(topics = INPUT_TOPIC, groupId = "TEXT_CONSUMERS")
    public void consumeMessage(String message) {
        Stream<WordFrequency> group = wordCounter.groupByWords(wordCounter.splitText(message));
        kafkaTemplate.send(OUTPUT_TOPIC, "KEY-1", new WordFrequencyList(group.toList()));
    }
}