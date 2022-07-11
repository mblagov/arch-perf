package com.mblagov.data.gen.strategy;

import org.bson.Document;

public class DataWithOperation {

    private Document data;
    private MongoOperation operation;

    public DataWithOperation(Document data, MongoOperation operation) {
        this.data = data;
        this.operation = operation;
    }

    public Document getData() {
        return data;
    }

    public void setData(Document data) {
        this.data = data;
    }

    public MongoOperation getOperation() {
        return operation;
    }

    public void setOperation(MongoOperation operation) {
        this.operation = operation;
    }
}
