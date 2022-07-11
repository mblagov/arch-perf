package com.mblagov.data.gen.strategy;

import com.mblagov.data.gen.convert.PersonConverter;
import com.mblagov.data.gen.generator.PersonGenerator;

import java.util.List;

public class PersonInsertOnlyStrategy implements DataGenStrategy {

    private final int numberOfRecords;

    public PersonInsertOnlyStrategy(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    @Override
    public List<DataWithOperation> records() {
        PersonGenerator generator = new PersonGenerator();

        return generator.generateRandomPeopleWithId(numberOfRecords)
                .stream()
                .map(PersonConverter::toDocumentWithId)
                .map(d -> new DataWithOperation(d, MongoOperation.INSERT))
                .toList();
    }
}
