-- 各種テーブル削除
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS tasks;

-- users テーブルを作成するクエリ
CREATE TABLE users (
id SERIAL PRIMARY KEY,
email VARCHAR(255),
name VARCHAR(20),
password VARCHAR(50),
need_graph boolean
);

-- tasks テーブルを作成するクエリ
CREATE TABLE tasks (
id SERIAL PRIMARY KEY,
category_id INTEGER,
user_id INTEGER,
title VARCHAR(255),
closing_date DATE,
progress INTEGER,
memo TEXT,
is_active BOOLEAN
);

-- categories テーブルを作成するクエリ
CREATE TABLE categories (
id SERIAL PRIMARY KEY,
name TEXT
);