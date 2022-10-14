CREATE SCHEMA IF NOT EXISTS 9gag DEFAULT CHARACTER SET utf8mb3;
USE 9gag;

CREATE TABLE IF NOT EXISTS users
(
    id         INT          NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    age        INT          NOT NULL,
    user_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL,
    is_active  TINYINT      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX email_UNIQUE (email),
    UNIQUE INDEX id_UNIQUE (id),
    UNIQUE INDEX user_name_UNIQUE (user_name)
);

CREATE TABLE IF NOT EXISTS categories
(
    id            INT          NOT NULL AUTO_INCREMENT,
    category_type VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS posts
(
    id            INT          NOT NULL AUTO_INCREMENT,
    title         VARCHAR(100) NOT NULL,
    resource_path VARCHAR(100) NOT NULL,
    created_at    DATETIME     NOT NULL,
    created_by    INT          NOT NULL,
    category_id   INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id),
    INDEX created_by_idx (created_by),
    INDEX category_id_idx (category_id),
    CONSTRAINT fk_posts_users_id
        FOREIGN KEY (created_by)
            REFERENCES users (id),
    CONSTRAINT fk_posts_categories_id
        FOREIGN KEY (category_id)
            REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id                 INT          NOT NULL AUTO_INCREMENT,
    text               VARCHAR(999) NOT NULL,
    created_at         DATETIME     NOT NULL,
    resource_path      VARCHAR(100) NULL DEFAULT NULL,
    replied_comment_id INT          NULL DEFAULT NULL,
    post_id            INT          NOT NULL,
    created_by         INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id),
    INDEX post_id_idx (post_id),
    INDEX created_by_idx (created_by),
    INDEX replied_commend_id_idx (replied_comment_id),
    CONSTRAINT fk_commends_users_id
        FOREIGN KEY (created_by)
            REFERENCES users (id),
    CONSTRAINT fk_commends_posts_id
        FOREIGN KEY (post_id)
            REFERENCES posts (id),
    CONSTRAINT fk_commends_commends_id
        FOREIGN KEY (replied_comment_id)
            REFERENCES comments (id)
);

CREATE TABLE IF NOT EXISTS users_comments_reactions
(
    status     TINYINT NOT NULL,
    user_id    INT     NOT NULL,
    comment_id INT     NOT NULL,
    INDEX user_id_idx (user_id),
    INDEX comment_id_idx (comment_id),
    CONSTRAINT fk_users_comments_reactions_comments_id
        FOREIGN KEY (comment_id)
            REFERENCES comments (id),
    CONSTRAINT fk_users_comments_reactions_users_id
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS users_posts_reactions
(
    status  TINYINT NOT NULL,
    user_id INT     NOT NULL,
    post_id INT     NOT NULL,
    INDEX user_id_idx (user_id),
    INDEX post_id_idx (post_id),
    CONSTRAINT fk_users_posts_reactions_posts_id
        FOREIGN KEY (post_id)
            REFERENCES posts (id),
    CONSTRAINT fk_users_posts_reactions_users_id
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS tags
(
    id       INT          NOT NULL AUTO_INCREMENT,
    tag_type VARCHAR(100) NOT NULL,
    post_id  INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id),
    INDEX post_id_idx (post_id),
    CONSTRAINT fk_tags_posts_id
        FOREIGN KEY (post_id)
            REFERENCES posts (id)
);