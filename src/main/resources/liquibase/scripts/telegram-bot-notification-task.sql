--liquibase formatted sql

--changeset ikovpik:1
CREATE TABLE notification_task
(
    id             BIGSERIAL PRIMARY KEY,
    message        VARCHAR(255) NOT NULL,
    scheduled_time TIMESTAMP    NOT NULL,
    recipient      VARCHAR(255) NOT NULL,
    is_sent        BOOLEAN      NOT NULL
);