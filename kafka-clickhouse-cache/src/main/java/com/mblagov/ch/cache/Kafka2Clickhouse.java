package com.mblagov.ch.cache;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mblagov.ch.cache.model.OplogMessage;
import com.mblagov.ch.cache.model.Person;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

public class Kafka2Clickhouse {

    private static final Map<String, Person> cache = new HashMap<>();

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:ch://mblagov-students-server:8123";
        Properties properties = new Properties();
        properties.setProperty("client_name", "Agent #1");

        String bootstrapServer = "mblagov-students-server:9092";
        String topicName = "person_data_6";

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        Gson gson = new GsonBuilder()
                .create();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement insertStatement = conn.prepareStatement(insertStatementSql());
             Statement statement = conn.createStatement();
             Consumer<Long, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topicName));

            while (true) {
                final ConsumerRecords<Long, String> consumerRecords =
                        consumer.poll(Duration.ofMillis(10000));

                consumerRecords.forEach(record -> {
                    OplogMessage message = gson.fromJson(record.value(), OplogMessage.class);

                    Person p = new Person(
                            message.getO().get_id(),
                            message.getO().getFirst_name(),
                            message.getO().getLast_name(),
                            message.getO().getMiddle_name(),
                            toDate(message.getO().getDate_of_birth()),
                            message.getO().getAddress(),
                            message.getO().getComment(),
                            toTimestamp(message.getTs()),
                            toTimestamp(record.timestamp()),
                            LocalDateTime.now(),
                            Objects.equals(message.getOp(), "d"),
                            toTimestamp(message.getO().getGenerated_ts())
                    );

                    cache.put(p.getId(), p);

                });
                System.out.println("Updated " + consumerRecords.count() + " records");

                statement.executeUpdate(dropTableSql());
                statement.executeUpdate(createTableSql());

                cache.entrySet().forEach(e -> {
                    try {
                        Person pers = e.getValue();
                        insertStatement.setString(1, pers.getId());
                        insertStatement.setString(2, pers.getFirstName());
                        insertStatement.setString(3, pers.getLastName());
                        insertStatement.setString(4, pers.getMiddleName());
                        insertStatement.setDate(5, Date.valueOf(pers.getDateOfBirth()));
                        insertStatement.setString(6, pers.getAddress());
                        insertStatement.setString(7, pers.getComment());
                        insertStatement.setTimestamp(8, Timestamp.valueOf(pers.getGeneratedTs()));
                        insertStatement.setTimestamp(9, Timestamp.valueOf(pers.getTs_wrote_to_mongo()));
                        insertStatement.setTimestamp(10, Timestamp.valueOf(pers.getTs_read_from_kafka()));
                        insertStatement.setTimestamp(11, Timestamp.valueOf(pers.getTs_wrote_to_ch()));
                        insertStatement.setBoolean(12, pers.getIs_deleted());
                        insertStatement.addBatch();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                insertStatement.executeBatch();
                consumer.commitAsync();
            }
        }
    }

    private static String dropTableSql() {
        return "drop table if exists mblagov.person_data_6";
    }

    private static String createTableSql() {
        return "create table if not exists  mblagov.person_data_6\n" +
                "(\n" +
                "    id                 String,\n" +
                "    first_name         String,\n" +
                "    last_name          String,\n" +
                "    middle_name        String,\n" +
                "    date_of_birth      datetime64,\n" +
                "    address            String,\n" +
                "    comment            String,\n" +
                "    ts_generated       datetime64,\n" +
                "    ts_wrote_to_mongo  datetime64,\n" +
                "    ts_read_from_kafka datetime64,\n" +
                "    ts_wrote_to_ch     datetime64,\n" +
                "    is_deleted         boolean\n" +
                ") ENGINE = MergeTree()\n" +
                "      ORDER BY id;";
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
                "is_deleted )\n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private static LocalDate toDate(Long value) {
        if (value == null) return null;
        return LocalDate.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
    }

    private static LocalDateTime toTimestamp(Long value) {
        if (value == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
    }
}
