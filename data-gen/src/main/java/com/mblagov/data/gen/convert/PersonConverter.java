package com.mblagov.data.gen.convert;

import com.mblagov.data.gen.model.Person;
import org.bson.Document;

public class PersonConverter {

    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String MIDDLE_NAME_KEY = "middle_name";
    public static final String DATE_OF_BIRTH_KEY = "date_of_birth";
    public static final String ADDRESS_KEY = "address";
    public static final String COMMENT_KEY = "comment";

    public static Document toDocument(Person p) {
        return new Document()
                .append(FIRST_NAME_KEY, p.getFirstName())
                .append(LAST_NAME_KEY, p.getLastName())
                .append(MIDDLE_NAME_KEY, p.getMiddleName())
                .append(DATE_OF_BIRTH_KEY, p.getDateOfBirth())
                .append(ADDRESS_KEY, p.getAddress())
                .append(COMMENT_KEY, p.getComment());
    }
}
