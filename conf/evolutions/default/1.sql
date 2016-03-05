# Tasks SCHEMA

# --- !Ups

CREATE SEQUENCE task_id_seq;
CREATE TABLE task (
  id     INTEGER NOT NULL DEFAULT nextval('task_id_seq'),
  label  VARCHAR(2000),
  who    VARCHAR(40),
  mytime VARCHAR(100),
  ready  BOOLEAN
);


INSERT INTO task VALUES (DEFAULT, 'TASK1', 'Yeah', 'Lol', FALSE);
INSERT INTO task VALUES (DEFAULT, 'TASK2', 'Yeah2', 'Lol', TRUE);

CREATE SEQUENCE user_id_seq;
CREATE TABLE users (
  id    INTEGER NOT NULL DEFAULT nextval('user_id_seq'),
  email VARCHAR(100),
  password VARCHAR(100),
  name VARCHAR(100),
  role VARCHAR(100)
);

INSERT INTO users VALUES (DEFAULT, 'a@a', 'a', 'User 1', 'Administrator');
INSERT INTO users VALUES (DEFAULT, 'b@b', 'a', 'User 2', 'User');

# --- !Downs

DROP TABLE task;
DROP SEQUENCE task_id_seq;
DROP TABLE users;
DROP SEQUENCE user_id_seq;