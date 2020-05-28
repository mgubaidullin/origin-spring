CREATE KEYSPACE IF NOT EXISTS origin WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE IF NOT EXISTS origin.sentences ( key varchar,sentence varchar, PRIMARY KEY (key) );