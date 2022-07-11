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

        String mongoClientUri = "mongodb://mblagov-students-server:27017/?replicaSet=rs0";
        String mongoDatabase = "uniform_data";
        String mongoCollection = "person_data_3";

        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoClientUri))) {
            database = mongoClient.getDatabase(mongoDatabase);
            MongoCollection<Document> collection = database.getCollection(mongoCollection);

            DataGenStrategy strategy = new PersonInsertOnlyStrategy(1000);
            List<DataWithOperation> records = strategy.records();

            collection.insertMany(records.stream().map(DataWithOperation::getData).toList());
        }
    }
}
