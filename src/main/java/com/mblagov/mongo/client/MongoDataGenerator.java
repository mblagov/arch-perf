package com.mblagov.mongo.client;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoDataGenerator {

    public static void main(String[] args) {
        MongoDatabase database;
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://students-server:27017"))) {
            database = mongoClient.getDatabase("uniform_data");
            MongoCollection<Document> collection = database.getCollection("person_data");
            List<Integer> books = Arrays.asList(27464, 747854);
            Document person = new Document()
                    .append("name", "Jo Bloggs")
                    .append("address", new BasicDBObject("street", "123 Fake St")
                            .append("city", "Faketon")
                            .append("state", "MA")
                            .append("zip", 12345))
                    .append("books", books);
            collection.insertOne(person);
        }
    }
}
