package com.mblagov.mongokafka;

import com.mongodb.CursorType;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.BsonTimestamp;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.internal.UnsignedLongs;
import org.bson.json.Converter;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Oplog2Kafka {

    public static void main(String[] args) {
        String bootstrapServer = "mblagov-students-server:9092";
        String topicName = "person_data_5";
        String mongoDatabase = "uniform_data";
        String mongoCollection = "person_data_5";
        String mongoClientUri = "mongodb://mblagov-students-server:27017/?replicaSet=rs0";

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        try (MongoClient mongoClient = MongoClients.create(mongoClientUri);
             Producer<String, String> producer = new KafkaProducer<>(props)) {

            BsonTimestamp lastTimeStamp = null;
            MongoDatabase localDb = mongoClient.getDatabase("local");
            MongoCollection<Document> oplog = localDb.getCollection("oplog.rs");

            Document filter = new Document();
            filter.put("ns", mongoDatabase + "." + mongoCollection);
            filter.put("op", new Document("$in", Arrays.asList("i", "u", "d")));
            if (lastTimeStamp != null) {
                filter.put("ts", new Document("$gt", lastTimeStamp));
            }
            Document projection = new Document("ts", 1).append("op", 1).append("o", 1);
            Document sort = new Document("$natural", 1);

            try (
                    MongoCursor<Document> cursor = oplog.find(filter)
                            .projection(projection)
                            .sort(sort)
                            .cursorType(CursorType.TailableAwait)
                            .noCursorTimeout(true)
                            .iterator()) {
                while (cursor.hasNext()) {
                    Document document = cursor.tryNext();
                    if (document == null) {
                        throw new RuntimeException("Next document is null");
                    }

                    JsonWriterSettings settings = JsonWriterSettings.builder().timestampConverter(
                            (value, writer) -> {
                                long unixTimestamp = value.getValue() >> 32;
                                Instant timestamp = Instant.ofEpochSecond(unixTimestamp);

                                writer.writeNumber(String.valueOf(timestamp.toEpochMilli()));
                            }
                    ).dateTimeConverter(
                            (value, writer) -> writer.writeNumber(String.valueOf(value))
                    ).build();
                    String value = document.toJson(settings);
                    producer.send(new ProducerRecord<>(topicName, value));

                    lastTimeStamp = (BsonTimestamp) document.get("ts");
                }
            }
        }
    }


}
