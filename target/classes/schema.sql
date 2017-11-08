-- Mysql schema setup
-- Using docker-compose to setup the database via docker-compose.yml
-- so the database itself is created through environment variables
-- otherwise would add the following here:
--  create database zeatful;

-- Drop the table and recreate if it already exists
DROP TABLE IF EXISTS server_logs;
DROP TABLE IF EXISTS offenders;

CREATE TABLE server_logs (
  entry_id BIGINT NOT NULL PRIMARY KEY  AUTO_INCREMENT,
  entry_time DATETIME(3),
  ip_address VARCHAR(15),
  request VARCHAR(255),
  request_status INT,
  user_agent VARCHAR(255)
);

CREATE TABLE offenders (
  entry_id BIGINT NOT NULL PRIMARY KEY  AUTO_INCREMENT,
  ip_address VARCHAR(15),
  request_count INT,
  reason VARCHAR(50)
);