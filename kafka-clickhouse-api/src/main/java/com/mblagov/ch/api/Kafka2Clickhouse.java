package com.mblagov.ch.api;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;
import java.time.*;
import java.util.Collections;
import java.util.Properties;

public class Kafka2Clickhouse {

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:ch://mblagov-students-server:8123";
        Properties properties = new Properties();
        properties.setProperty("client_name", "Agent #1");

        String sql = "insert into mblagov.person_data (" +
                "id, " +
                "first_name, " +
                "last_name, " +
                "middle_name, " +
                "date_of_birth, " +
                "address, " +
                "comment," +
                "ts_generated," +
                "ts_wrote_to_mongo," +
                "ts_read_from_kafka," +
                "ts_wrote_to_ch )\n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String bootstrapServer = "mblagov-students-server:9092";
        String topicName = "person_data";

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        Gson gson = new GsonBuilder()
                .create();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Consumer<Long, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topicName));

            while (true) {
                final ConsumerRecords<Long, String> consumerRecords =
                        consumer.poll(Duration.ofMillis(1000));

                consumerRecords.forEach(record -> {
                    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                            record.key(), record.value(),
                            record.partition(), record.offset());

                    try {
                        OplogMessage message = gson.fromJson(record.value(), OplogMessage.class);
                        stmt.setString(1, message.getO().get_id());
                        stmt.setString(2, message.getO().getFirst_name());
                        stmt.setString(3, message.getO().getLast_name());
                        stmt.setString(4, message.getO().getMiddle_name());
                        stmt.setDate(5, toDate(message.getO().getDate_of_birth()));
                        stmt.setString(6, message.getO().getAddress());
                        stmt.setString(7, message.getO().getComment());
                        stmt.setTimestamp(8, toTimestamp(message.getO().getGenerated_ts()));
                        stmt.setTimestamp(9, toTimestamp(message.getTs()));
                        stmt.setTimestamp(10, toTimestamp(record.timestamp()));
                        stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                int updatedRecords = stmt.executeUpdate();
                System.out.printf("Updated %s records %n", updatedRecords);

                consumer.commitAsync();
            }
        }

    }

    private static Date toDate(Long value) {
        return Date.valueOf(LocalDate.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC));
    }

    private static Timestamp toTimestamp(Long value) {
        return Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC));
    }
}
