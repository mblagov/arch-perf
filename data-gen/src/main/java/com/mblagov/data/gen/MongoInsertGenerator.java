package com.mblagov.data.gen;

import com.mblagov.data.gen.strategy.DataGenStrategy;
import com.mblagov.data.gen.strategy.DataWithOperation;
import com.mblagov.data.gen.strategy.PersonInsertOnlyStrategy;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoInsertGenerator {

    public static void main(String[] args) {
        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://mblagov-students-server:27017/?replicaSet=rs0"))) {
            database = mongoClient.getDatabase("uniform_data");
            MongoCollection<Document> collection = database.getCollection("person_data");

            DataGenStrategy strategy = new PersonInsertOnlyStrategy(1000);
            List<DataWithOperation> records = strategy.records();

            collection.insertMany(records.stream().map(DataWithOperation::getData).toList());
        }
    }
}
