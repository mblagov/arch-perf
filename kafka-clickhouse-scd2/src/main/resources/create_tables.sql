create database if not exists  mblagov;

drop table if exists mblagov.person_data_6;

create table if not exists  mblagov.person_data_6
(
    id                 String,
    first_name         String,
    last_name          String,
    middle_name        String,
    date_of_birth      datetime64,
    address            String,
    comment            String,
    ts_generated       datetime64,
    ts_wrote_to_mongo  datetime64,
    ts_read_from_kafka datetime64,
    ts_wrote_to_ch     datetime64,
    is_deleted         boolean,
    row_version            Int64
) ENGINE = MergeTree()
      ORDER BY id;