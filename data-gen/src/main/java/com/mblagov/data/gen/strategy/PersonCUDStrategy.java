package com.mblagov.data.gen.strategy;

import com.mongodb.MongoClient;

import java.util.List;

public class PersonCUDStrategy implements DataGenStrategy {

    private MongoClient mongoClient;
    private String database;
    private String collection;
    private int insertsPercentage;
    private int updatesPercentage;
    private int deletesPercentage;

    public PersonCUDStrategy(MongoClient mongoClient, String database, String collection, int insertsPercentage, int updatesPercentage, int deletesPercentage) {
        if (insertsPercentage + updatesPercentage + deletesPercentage != 100) {
            throw new IllegalArgumentException("Percentages must be 100 in sum!");
        }

        this.mongoClient = mongoClient;
        this.database = database;
        this.collection = collection;
        this.insertsPercentage = insertsPercentage;
        this.updatesPercentage = updatesPercentage;
        this.deletesPercentage = deletesPercentage;
    }

    @Override
    public List<DataWithOperation> records() {


        return null;
    }
}
