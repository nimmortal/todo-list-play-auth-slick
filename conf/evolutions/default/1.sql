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

CREATE TABLE users (
  id    INTEGER AUTO_INCREMENT PRIMARY KEY,
  role  VARCHAR(100)
);

INSERT INTO users VALUES (0, 'Administrator');
INSERT INTO users VALUES (1, 'User');

CREATE TABLE registered_users (
  user_id INTEGER NOT NULL UNIQUE REFERENCES users(id),
  email VARCHAR(100) NOT NULL UNIQUE,
  username VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  name VARCHAR(100),
  surname VARCHAR(100),
  location VARCHAR(100),
  avatar_url VARCHAR(100)
);

INSERT INTO registered_users VALUES (0, 'a@a', 'a', 'a', 'admin', 'adminovich', NULL, '');
INSERT INTO registered_users VALUES (1, 'b@b', 'b', 'b', NULL, NULL, NULL, '');

CREATE TABLE facebook_users (
  user_id INTEGER NOT NULL UNIQUE REFERENCES users(id),
  id VARCHAR(100) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  avatar_url VARCHAR(1000) NOT NULL,
  access_token VARCHAR(1000) NOT NULL
);

# --- !Downs

DROP TABLE task;
DROP TABLE users;
DROP TABLE registered_users;
DROP TABLE facebook_users;