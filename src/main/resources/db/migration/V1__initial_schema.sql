CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE forms (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_by  BIGINT       NOT NULL REFERENCES users(id),
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

CREATE TABLE questions (
    id       BIGSERIAL PRIMARY KEY,
    form_id  BIGINT       NOT NULL REFERENCES forms(id) ON DELETE CASCADE,
    text     TEXT         NOT NULL,
    type     VARCHAR(30)  NOT NULL,
    position INTEGER      NOT NULL,
    required BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE question_options (
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT       NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    text        VARCHAR(255) NOT NULL,
    position    INTEGER      NOT NULL
);

CREATE TABLE form_distributions (
    id      BIGSERIAL PRIMARY KEY,
    form_id BIGINT    NOT NULL REFERENCES forms(id),
    sent_by BIGINT    NOT NULL REFERENCES users(id),
    sent_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recipients (
    id              BIGSERIAL PRIMARY KEY,
    distribution_id BIGINT       NOT NULL REFERENCES form_distributions(id) ON DELETE CASCADE,
    email           VARCHAR(255) NOT NULL,
    slack_user_id   VARCHAR(255),
    token           VARCHAR(255) NOT NULL UNIQUE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    sent_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMP
);

CREATE TABLE survey_responses (
    id           BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT    NOT NULL REFERENCES recipients(id),
    form_id      BIGINT    NOT NULL REFERENCES forms(id),
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE answers (
    id                 BIGSERIAL PRIMARY KEY,
    survey_response_id BIGINT NOT NULL REFERENCES survey_responses(id) ON DELETE CASCADE,
    question_id        BIGINT NOT NULL REFERENCES questions(id),
    value              TEXT   NOT NULL
);
