package com.mblagov.data.gen.strategy;

import com.mblagov.data.gen.convert.PersonConverter;
import com.mblagov.data.gen.generator.PersonGenerator;
import com.mblagov.data.gen.model.Person;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class PersonCUDStrategyStandalone implements DataGenStrategy {

    private int insertsCount;
    private int updatesCount;
    private int deletesCount;

    public PersonCUDStrategyStandalone(int insertsCount, int updatesCount, int deletesCount) {
        this.insertsCount = insertsCount;
        this.updatesCount = updatesCount;
        this.deletesCount = deletesCount;
    }

    @Override
    public List<DataWithOperation> records() {
        PersonGenerator generator = new PersonGenerator();
        List<Person> insertRecords = generator.generateRandomPeopleWithId(this.insertsCount);

        List<Person> updateRecords;
        if (updatesCount <= insertsCount) {
            List<Person> limitedInserts = insertRecords.stream().limit(updatesCount).toList();
            updateRecords = refreshWithTheSameIds(limitedInserts, generator);
        } else {
            int fullIters = updatesCount / insertsCount;
            int remaining = updatesCount % insertsCount;
            updateRecords = new ArrayList<>();
            for (int i = 0; i < fullIters; i++) {
                List<Person> recordsToAdd = refreshWithTheSameIds(insertRecords, generator);
                updateRecords.addAll(recordsToAdd);
            }
            List<Person> limitedInserts = insertRecords.stream().limit(remaining).toList();
            List<Person> recordsToAdd = refreshWithTheSameIds(limitedInserts, generator);
            updateRecords.addAll(recordsToAdd);
        }

        List<Person> deleteRecords;

        if (deletesCount <= insertsCount){
            deleteRecords = insertRecords.stream().limit(deletesCount).toList();
        } else {
            throw new IllegalStateException("Can't prepare for deletion more than was inserted");
        }

        Stream<DataWithOperation> insertsStream = insertRecords.stream()
                .map(PersonConverter::toDocumentWithId)
                .map(p -> new DataWithOperation(p, MongoOperation.INSERT));
        Stream<DataWithOperation> updatesStream = updateRecords.stream()
                .map(PersonConverter::toDocumentWithId)
                .map(p -> new DataWithOperation(p, MongoOperation.UPDATE));
        Stream<DataWithOperation> deletesStream = deleteRecords.stream()
                .map(PersonConverter::toDocumentWithId)
                .map(p -> new DataWithOperation(p, MongoOperation.DELETE));
        return Stream.concat(Stream.concat(insertsStream, updatesStream), deletesStream)
                .sorted(new Comparator<DataWithOperation>() {
                    @Override
                    public int compare(DataWithOperation o1, DataWithOperation o2) {
                        int idsComparison = PersonConverter.getId(o1.getData()).compareTo(PersonConverter.getId(o2.getData()));
                        if (idsComparison == 0) {
                            return o1.getOperation().ordinal() - o2.getOperation().ordinal();
                        } else {
                            return idsComparison;
                        }
                    }
                })
                .toList();
    }


    private List<Person> refreshWithTheSameIds(List<Person> source, PersonGenerator generator) {
        return source.stream()
                .map(p -> generator.getRandomPersonWithId(p.getId()))
                .toList();
    }
}
