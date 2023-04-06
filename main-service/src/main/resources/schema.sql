CREATE TABLE IF NOT EXISTS users
(
    user_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name   VARCHAR(100)                            NOT NULL,
    email       VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY(user_id),
    CONSTRAINT unique_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS categories (
    category_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name   VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY(category_id),
    CONSTRAINT uk_category_name UNIQUE (category_name)
    );

CREATE TABLE IF NOT EXISTS events (
    event_id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation          VARCHAR(2000)                           NOT NULL,
    category_id         BIGINT                                  NOT NULL,
    confirmed_requests  INTEGER,
    create_date         TIMESTAMP WITHOUT TIME ZONE,
    description         VARCHAR(7000),
    event_date          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id        BIGINT                                  NOT NULL,
    lat                 NUMERIC                                 NOT NULL,
    lon                 NUMERIC                                 NOT NULL,
    paid                BOOLEAN,
    participant_limit   INTEGER DEFAULT 0,
    request_moderation  BOOLEAN,
    title               VARCHAR(120)                            NOT NULL,
    views               INTEGER,
    published_date      TIMESTAMP WITHOUT TIME ZONE,
    status              VARCHAR(200),
    CONSTRAINT pk_event PRIMARY KEY(event_id),
    CONSTRAINT fk_event_on_user FOREIGN KEY(initiator_id) REFERENCES users(user_id),
    CONSTRAINT fk_event_on_category FOREIGN KEY(category_id) REFERENCES categories(category_id) ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS requests (
    request_id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id        BIGINT                                  NOT NULL,
    requester_id    BIGINT                                  NOT NULL,
    create_date     TIMESTAMP WITHOUT TIME ZONE,
    status          VARCHAR(20),
    CONSTRAINT pk_requests PRIMARY KEY(request_id),
    CONSTRAINT fk_requests_on_event FOREIGN KEY(event_id) REFERENCES events(event_id),
    CONSTRAINT fk_requests_on_user FOREIGN KEY(requester_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned          BOOLEAN,
    title           VARCHAR(120),
    CONSTRAINT pk_compilations PRIMARY KEY(compilation_id)
    );

CREATE TABLE IF NOT EXISTS event_compilation (
    event_id            BIGINT NOT NULL,
    compilation_id      BIGINT NOT NULL,
    CONSTRAINT pk_event_compilation PRIMARY KEY (event_id ,compilation_id),
    CONSTRAINT fk_event_compilation_on_event FOREIGN KEY(event_id) REFERENCES events(event_id) ON UPDATE CASCADE,
    CONSTRAINT fk_event_compilation_on_compilation FOREIGN KEY(compilation_id) REFERENCES compilations(compilation_id) ON UPDATE CASCADE
    );

