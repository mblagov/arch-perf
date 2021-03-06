package com.mblagov.ch.scd2;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mblagov.ch.scd2.model.OplogMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;
import java.time.*;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

public class Kafka2Clickhouse {

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:ch://mblagov-students-server:8123";
        Properties properties = new Properties();
        properties.setProperty("client_name", "Agent #1");

        String bootstrapServer = "mblagov-students-server:9092";
        String topicName = "person_data_6";

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        Gson gson = new GsonBuilder()
                .create();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement insertStatement = conn.prepareStatement(insertStatementSql());
             PreparedStatement selectStatement = conn.prepareStatement(selectStatementSql());
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
                        selectStatement.setString(1, message.getO().get_id());
                        ResultSet resultSet = selectStatement.executeQuery();

                        long version = 1;
                        if (resultSet.next()) {
                            version = resultSet.getLong(1) + 1;
                        }

                        insertStatement.setString(1, message.getO().get_id());
                        insertStatement.setString(2, message.getO().getFirst_name());
                        insertStatement.setString(3, message.getO().getLast_name());
                        insertStatement.setString(4, message.getO().getMiddle_name());
                        insertStatement.setDate(5, toDate(message.getO().getDate_of_birth()));
                        insertStatement.setString(6, message.getO().getAddress());
                        insertStatement.setString(7, message.getO().getComment());
                        insertStatement.setTimestamp(8, toTimestamp(message.getO().getGenerated_ts()));
                        insertStatement.setTimestamp(9, toTimestamp(message.getTs()));
                        insertStatement.setTimestamp(10, toTimestamp(record.timestamp()));
                        insertStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                        insertStatement.setBoolean(12, false);
                        insertStatement.setLong(13, version);
                        insertStatement.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                System.out.println("Updated " + consumerRecords.count() + " records");
                consumer.commitAsync();
            }
        }
    }

    private static String selectStatementSql() {
        return "select id from (select " +
                "row_version," +
                "id, " +
                "row_number() over (partition by id order by row_version desc) as rn " +
                "from mblagov.person_data_6 " +
                "where id = ?) where rn = 1";
    }

    private static String insertStatementSql() {
        return "insert into mblagov.person_data_6 (" +
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
                "ts_wrote_to_ch," +
                "is_deleted, " +
                "row_version )\n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private static Date toDate(Long value) {
        if (value == null) return null;
        return Date.valueOf(LocalDate.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC));
    }

    private static Timestamp toTimestamp(Long value) {
        if (value == null) return null;
        return Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC));
    }
}
