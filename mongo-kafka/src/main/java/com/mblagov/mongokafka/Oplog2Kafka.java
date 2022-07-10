package com.mblagov.mongokafka;

import com.mongodb.CursorType;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.BsonTimestamp;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Oplog2Kafka {

    public static void main(String[] args) {
        String topicName = "person_data";

        Properties props = new Properties();
        props.put("bootstrap.servers", "mblagov-students-server:9092");
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        String mongoDatabaseName = "uniform_data";
        String collectionName = "person_data";
        try (MongoClient mongoClient = MongoClients.create("mongodb://mblagov-students-server:27017/?replicaSet=rs0");
             Producer<String, String> producer = new KafkaProducer<String, String>(props)) {


            BsonTimestamp lastTimeStamp = null;
            MongoDatabase localDb = mongoClient.getDatabase("local");
            MongoCollection<Document> oplog = localDb.getCollection("oplog.rs");

            List<Document> opLogList = new ArrayList<>();

            Document filter = new Document();
            filter.put("ns", mongoDatabaseName + "." + collectionName);
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
                    if (document == null) throw new RuntimeException("Next document is null");
                    String value = document.toJson();
                    System.out.println(value);
                    producer.send(new ProducerRecord<>("mblagov", value));

                    // TODO save last timestamp properly
                    lastTimeStamp = (BsonTimestamp) document.get("ts");
                }
            }
        }
    }


}
