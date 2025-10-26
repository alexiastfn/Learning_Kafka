
# Flow

## 1. kafkaapp-producer (TextProducer)
`Port`: 8080

`Produces to`: TEXT-DATA topic

`What it does`: Receives files via HTTP and sends each line to Kafka

`In Kafka UI`: You see raw messages in TEXT-DATA/Messages

## 2. kafkaapp-consumer (TextConsumer)
`Port`: 8082 (assumed)

`Consumes from`: TEXT-DATA topic

`Produces to`: AGGREGATE-DATA topic

`What it does`: Takes each line, counts words, sends frequencies

`In Kafka UI`: You see TEXT-DATA/Consumers → TEXT_CONSUMERS

## 3. kafkaapp-aggregator (TextAggregator)
`Port`: 8086

`Consumes from`: AGGREGATE-DATA topic

`What it does`: Aggregates all frequencies and keeps them in memory

`In Kafka UI`: You see AGGREGATE-DATA/Consumers → AGGREGATE_CONSUMERS
