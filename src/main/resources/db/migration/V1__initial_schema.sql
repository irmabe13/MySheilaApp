SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE roles (
                       id_role      INT AUTO_INCREMENT PRIMARY KEY,
                       name         VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
                       id_user      INT AUTO_INCREMENT PRIMARY KEY,
                       firstname    VARCHAR(50)  NOT NULL,
                       lastname     VARCHAR(100) NOT NULL,
                       email        VARCHAR(150) NOT NULL,
                       password     VARCHAR(255) NOT NULL,
                       create_at    DATE         NOT NULL,
                       modified_at  DATE         NOT NULL,
                       enabled      TINYINT(1)   NOT NULL DEFAULT 1,
                       id_role      INT          NOT NULL,
                       CONSTRAINT fk_users_role FOREIGN KEY (id_role)
                           REFERENCES roles(id_role) ON DELETE RESTRICT ON UPDATE CASCADE,
                       UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tokens (
                        id_token   INT AUTO_INCREMENT PRIMARY KEY,
                        token      VARCHAR(255) NOT NULL,
                        token_type VARCHAR(255) NOT NULL,
                        revoked    TINYINT(1)   NOT NULL DEFAULT 0,
                        expired    TINYINT(1)   NOT NULL DEFAULT 0,
                        id_user    INT          NOT NULL,
                        CONSTRAINT fk_tokens_user FOREIGN KEY (id_user)
                            REFERENCES users(id_user) ON DELETE CASCADE ON UPDATE CASCADE,
                        UNIQUE KEY uk_tokens_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
                            id_category INT AUTO_INCREMENT PRIMARY KEY,
                            name        VARCHAR(255) NOT NULL,
                            UNIQUE KEY uk_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE goals (
                       id_goal     INT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(255) NOT NULL,
                       periodicity VARCHAR(50)  NOT NULL,
                       frequency   INT          NOT NULL,
                       id_category INT          NOT NULL,
                       CONSTRAINT fk_goals_category FOREIGN KEY (id_category)
                           REFERENCES categories(id_category) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users_goals (
                             id_user INT NOT NULL,
                             id_goal INT NOT NULL,
                             PRIMARY KEY (id_user, id_goal),
                             CONSTRAINT fk_users_goals_user FOREIGN KEY (id_user)
                                 REFERENCES users(id_user) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT fk_users_goals_goal FOREIGN KEY (id_goal)
                                 REFERENCES goals(id_goal) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE plannings (
                           id_planning INT AUTO_INCREMENT PRIMARY KEY,
                           hour_begin  TIME NOT NULL,
                           hour_end    TIME NOT NULL,
                           status      VARCHAR(20) NOT NULL,
                           CHECK (hour_begin < hour_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE availabilities (
                                id_availability INT AUTO_INCREMENT PRIMARY KEY,
                                hour_begin      TIME        NOT NULL,
                                hour_end        TIME        NOT NULL,
                                day             VARCHAR(20) NOT NULL,
                                id_user         INT         NOT NULL,
                                CHECK (hour_begin < hour_end),
                                CONSTRAINT fk_avail_user FOREIGN KEY (id_user)
                                    REFERENCES users(id_user) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE tasks (
                       id_task     INT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(255) NOT NULL,
                       description VARCHAR(255),
                       duration    TIME,
                       hour_date   DATETIME,
                       status      VARCHAR(20)  NOT NULL,
                       id_goal     INT          NOT NULL,
                       CONSTRAINT fk_tasks_goal FOREIGN KEY (id_goal)
                           REFERENCES goals(id_goal) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE plannings_tasks (
                                 id_planning INT NOT NULL,
                                 id_task     INT NOT NULL,
                                 PRIMARY KEY (id_planning, id_task),
                                 CONSTRAINT fk_pt_planning FOREIGN KEY (id_planning)
                                     REFERENCES plannings(id_planning) ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT fk_pt_task FOREIGN KEY (id_task)
                                     REFERENCES tasks(id_task) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;