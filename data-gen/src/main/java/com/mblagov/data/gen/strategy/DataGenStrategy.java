package com.mblagov.data.gen.strategy;

import com.mblagov.data.gen.model.Person;

import java.util.List;

public interface DataGenStrategy {

    public List<DataWithOperation> records();
}
