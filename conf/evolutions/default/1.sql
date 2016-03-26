# Tasks SCHEMA

# --- !Ups

CREATE TABLE task (
  id     INTEGER AUTO_INCREMENT PRIMARY KEY,
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

CREATE TABLE accounts (
  id    INTEGER AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100),
  password VARCHAR(100),
  role VARCHAR(100)
);

INSERT INTO accounts VALUES (DEFAULT, 'a@a', 'a', 'Administrator');
INSERT INTO accounts VALUES (DEFAULT, 'b@b', 'b', 'User');

CREATE TABLE users (
  email VARCHAR(100) REFERENCES accounts(email),
  name VARCHAR(100),
  surname VARCHAR(100),
  address VARCHAR(100),
);

INSERT INTO users VALUES ('a@a', 'A-name', 'A-surname', 'A-Empty Street');
INSERT INTO users VALUES ('b@b', 'B-name', 'B-surname', 'B-Empty Street');

CREATE TABLE facebook_users (
  user_id INTEGER NOT NULL UNIQUE REFERENCES accounts(id),
  id VARCHAR(100) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  cover_url VARCHAR(1000) NOT NULL,
  access_token VARCHAR(1000) NOT NULL
);

# --- !Downs

DROP TABLE task;
DROP TABLE users;
DROP TABLE facebook_users;