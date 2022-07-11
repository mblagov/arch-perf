package com.mblagov.data.gen;

import com.mblagov.data.gen.convert.PersonConverter;
import com.mblagov.data.gen.strategy.DataGenStrategy;
import com.mblagov.data.gen.strategy.DataWithOperation;
import com.mblagov.data.gen.strategy.PersonCUDStrategyStandalone;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoDataGenerator {

    public static void main(String[] args) {

        String mongoClientUri = "mongodb://mblagov-students-server:27017/?replicaSet=rs0";
        String mongoDatabase = "uniform_data";
        String mongoCollection = "person_data_6";

        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoClientUri))) {
            database = mongoClient.getDatabase(mongoDatabase);
            MongoCollection<Document> collection = database.getCollection(mongoCollection);

            DataGenStrategy strategy = new PersonCUDStrategyStandalone(20, 40, 10);
            List<DataWithOperation> records = strategy.records();

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
