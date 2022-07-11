package com.mblagov.data.gen;

import com.mblagov.data.gen.generator.PersonGenerator;
import com.mblagov.data.gen.convert.PersonConverter;
import com.mblagov.data.gen.strategy.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.List;

public class MongoDataGenerator {

    public static void main(String[] args) {

        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://mblagov-students-server:27017/?replicaSet=rs0"))) {
            database = mongoClient.getDatabase("uniform_data");
            MongoCollection<Document> collection = database.getCollection("person_data");

            DataGenStrategy strategy = new PersonCUDStrategyStandalone(400, 400, 200);
            List<DataWithOperation> records = strategy.records();

            collection.deleteMany(new BsonDocument());

            for (DataWithOperation record : records) {
                switch (record.getOperation()) {
                    case INSERT -> {
                        System.out.println("inserting " + PersonConverter.getId(record.getData()));
                        collection.insertOne(record.getData());
                    }
                    case UPDATE -> {
                        System.out.println("updating " + PersonConverter.getId(record.getData()));
                        collection.replaceOne(PersonConverter.getIdDocument(record.getData()), record.getData());
                    }
                    case DELETE -> {
                        System.out.println("deleting " + PersonConverter.getId(record.getData()));
                        collection.deleteOne(PersonConverter.getIdDocument(record.getData()));
                    }
                }
            }
        }
    }
}
