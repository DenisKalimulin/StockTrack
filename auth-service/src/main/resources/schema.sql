CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    login VARCHAR(128) UNIQUE NOT NULL,
    email VARCHAR(128) UNIQUE NOT NULL,
    password VARCHAR(64) NOT NULL
);

-- Индексы для login и email
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_login ON users (login);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);