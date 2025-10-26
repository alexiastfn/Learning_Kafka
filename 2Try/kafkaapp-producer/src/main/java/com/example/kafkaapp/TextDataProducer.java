package com.example.kafkaapp;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.yaml.snakeyaml.nodes.Tag.STR;

@Component
public class TextDataProducer {
    Logger logger = Logger.getLogger(getClass().getName());

    // Constants for topic configuration
    private final static int PARTITION_COUNT = 8;
    private final static String TOPIC = "TEXT-DATA";
    private final static short REPLICATION_FACTOR = 1;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TextDataProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void configureTopic(KafkaAdmin kafkaAdmin) {
        kafkaAdmin.createOrModifyTopics(new NewTopic(TOPIC, PARTITION_COUNT, REPLICATION_FACTOR));
    }

    private void sendTextMessage(String text, int lineIndex) {
        if (text == null || text.isEmpty()) {
            return;
        }
        // Sends the Link message to the topic, distributing across partitions based on the line index
        kafkaTemplate.send(TOPIC, "KEY-" + (lineIndex % PARTITION_COUNT), text);
    }

    public void sendContentOf(File file) {
        Instant before = Instant.now();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            AtomicInteger counter = new AtomicInteger(0);
            lines.forEach(line -> sendTextMessage(line, counter.getAndIncrement()));
            Instant after = Instant.now();
            Duration duration = Duration.between(before, after);
            logger.info(String.format("Streamed %d lines in %d milliseconds", counter.get(), duration.toMillis()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}