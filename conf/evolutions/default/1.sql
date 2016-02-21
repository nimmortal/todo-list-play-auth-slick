# Tasks schema

# --- !Ups

CREATE SEQUENCE task_id_seq;
CREATE TABLE task (
    id integer NOT NULL DEFAULT nextval('task_id_seq'),
    label varchar(2000),
    who varchar(40),
    mytime varchar(100),
    ready boolean
);

CREATE TABLE users(
  id VARCHAR(10)
);

INSERT INTO task VALUES (DEFAULT, 'TASK1', 'Yeah', 'Lol', false);
INSERT INTO task VALUES (DEFAULT, 'TASK2', 'Yeah2', 'Lol', true);

# --- !Downs

DROP TABLE task;
DROP SEQUENCE task_id_seq;