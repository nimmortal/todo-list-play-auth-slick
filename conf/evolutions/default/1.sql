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


INSERT INTO task VALUES (DEFAULT, 'Test task 1', 'System', NOW(), FALSE);
INSERT INTO task VALUES (DEFAULT, 'Test task 2', 'System', NOW(), FALSE);
INSERT INTO task VALUES (DEFAULT, 'Test task 3', 'System', NOW(), FALSE);
INSERT INTO task VALUES (DEFAULT, 'Test task 4', 'System', NOW(), FALSE);
INSERT INTO task VALUES (DEFAULT, 'Test task 5', 'System', NOW(), TRUE);
INSERT INTO task VALUES (DEFAULT, 'Test task 6', 'System', NOW(), TRUE);

CREATE SEQUENCE account_id_seq;
CREATE TABLE accounts (
  id    INTEGER NOT NULL DEFAULT nextval('account_id_seq'),
  email VARCHAR(100),
  password VARCHAR(100),
  role VARCHAR(100)
);

INSERT INTO accounts VALUES (DEFAULT, 'a@a', 'a', 'Administrator');
INSERT INTO accounts VALUES (DEFAULT, 'b@b', 'b', 'User');

CREATE TABLE users (
  email VARCHAR(100),
  name VARCHAR(100),
  surname VARCHAR(100),
  address VARCHAR(100),
  FOREIGN KEY (email) REFERENCES accounts(email)
);

INSERT INTO users VALUES ('a@a', 'A-name', 'A-surname', 'A-Empty Street');
INSERT INTO users VALUES ('b@b', 'B-name', 'B-surname', 'B-Empty Street');

# --- !Downs

DROP TABLE task;
DROP SEQUENCE task_id_seq;
DROP TABLE users;
DROP SEQUENCE account_id_seq;