package com.mblagov.mongo.client;

import com.mblagov.mongo.client.convert.PersonConverter;
import com.mblagov.mongo.client.generator.PersonGenerator;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoDataGenerator {

    public static void main(String[] args) {

        int numberOfRecords = 256;

        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://mblagov-students-server:27017/?replicaSet=rs0"))) {
            database = mongoClient.getDatabase("uniform_data");
            MongoCollection<Document> collection = database.getCollection("person_data");
            PersonGenerator generator = new PersonGenerator();
            List<Document> inserts = generator.generateRandomPersons(numberOfRecords).stream().map(PersonConverter::toDocument).toList();

            collection.insertMany(inserts);
        }
    }
}
