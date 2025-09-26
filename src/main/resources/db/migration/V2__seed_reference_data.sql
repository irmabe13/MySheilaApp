INSERT INTO roles (name) VALUES ('USER'), ('ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES
                                  ('Bien-être physique'), ('Bien-être mental'), ('Spiritualité')
ON DUPLICATE KEY UPDATE name = VALUES(name);
