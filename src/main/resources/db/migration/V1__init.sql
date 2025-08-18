CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(200) NOT NULL,
                       firstname VARCHAR(100),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE tokens (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        jti VARCHAR(64) NOT NULL UNIQUE,
                        user_id BIGINT NOT NULL,
                        revoked BOOLEAN NOT NULL DEFAULT FALSE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE goals (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                category_id BIGINT NOT NULL,
                                name VARCHAR(120) NOT NULL,
                                frequency_type VARCHAR(20) NOT NULL,
                                frequency_count INT NOT NULL,
                                default_duration_minutes INT NOT NULL,
                                CONSTRAINT fk_gt_cat FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE user_goals (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            goal_template_id BIGINT NOT NULL,
                            UNIQUE (user_id, goal_template_id),
                            CONSTRAINT fk_ug_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_ug_gt FOREIGN KEY (goal_template_id) REFERENCES goals(id) ON DELETE CASCADE
);

CREATE TABLE availabilities (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                day_of_week VARCHAR(10) NOT NULL,
                                slot VARCHAR(20) NOT NULL,
                                UNIQUE (user_id, day_of_week, slot),
                                CONSTRAINT fk_av_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE plans (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       CONSTRAINT fk_plan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE planned_tasks (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               plan_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               goal_id BIGINT NOT NULL,
                               task_date DATE NOT NULL,
                               slot VARCHAR(20) NOT NULL,
                               duration_minutes INT NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               carried_over BOOLEAN NOT NULL DEFAULT FALSE,
                               CONSTRAINT fk_pt_plan FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE,
                               CONSTRAINT fk_pt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_pt_gt FOREIGN KEY (goal_id) REFERENCES goals(id)
);


INSERT INTO roles(name) VALUES ('USER');

INSERT INTO categories(name) VALUES ('Spiritualité'), ('Bien-être physique'), ('Bien-être mental');

INSERT INTO goals(category_id, name, frequency_type, frequency_count, default_duration_minutes) VALUES
                                                                                                             ((SELECT id FROM categories WHERE name='Spiritualité'), 'Moment de spiritualité quotidien', 'DAILY', 1, 10),
                                                                                                             ((SELECT id FROM categories WHERE name='Spiritualité'), 'Pratiquer la gratitude', 'DAILY', 1, 5),
                                                                                                             ((SELECT id FROM categories WHERE name='Spiritualité'), 'Méditation / Réflexion', 'DAILY', 1, 15),
                                                                                                             ((SELECT id FROM categories WHERE name='Spiritualité'), 'Approfondir ma spiritualité', 'WEEKLY', 3, 20);