CREATE TABLE SOURCE (id serial, dir varchar NOT NULL, last_scanned timestamp);
CREATE UNIQUE INDEX idx_source_uq ON source (dir);
CREATE UNIQUE INDEX idx_source_id ON source (id);

CREATE TABLE RESULT (
           id serial,
           dir_id int references source(id) NOT NULL,
           file_name varchar,
           words varchar[] NOT NULL);
CREATE UNIQUE INDEX idx_result_id ON result (id);
CREATE UNIQUE INDEX idx_result_uq ON result (dir_id, file_name);