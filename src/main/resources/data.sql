-- users テーブルにデータを挿入するクエリ
INSERT INTO users (email, name, password, need_graph)
VALUES
('tanaka@aaa.com', '田中太郎', 'test123', 'f'),
('suzuki@aaa.com', '鈴木一郎', 'test456', 'f');

-- tasks テーブルにデータを挿入するクエリ
INSERT INTO tasks (category_id, user_id, title, closing_date, progress, memo, is_active)
VALUES
(1, 1, '見積もり', '2025-12-31', 0, '案件に適した見積もりを取る', 'yes');

-- categories テーブルにデータを挿入するクエリ
INSERT INTO categories (name)
VALUES
('仕事'),
('趣味'),
('買い物'),
('その他');