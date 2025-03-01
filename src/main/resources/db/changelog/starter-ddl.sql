--changeset author:create-locales-table
CREATE TABLE locales (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(10) NOT NULL UNIQUE,
                         name VARCHAR(50) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         INDEX idx_locale_code (code)
);
--rollback DROP TABLE locales;

--changeset author:create-tags-table
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      INDEX idx_tag_name (name)
);
--rollback DROP TABLE tags;

--changeset author:create-translations-table
CREATE TABLE translations (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              translation_key VARCHAR(255) NOT NULL,
                              content TEXT NOT NULL,
                              locale_id BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (locale_id) REFERENCES locales(id),
                              INDEX idx_translation_key (translation_key),
                              INDEX idx_locale_id (locale_id),
                              UNIQUE KEY uk_key_locale (translation_key, locale_id)
);
--rollback DROP TABLE translations;

--changeset author:create-translation-tags-table
CREATE TABLE translation_tags (
                                  translation_id BIGINT NOT NULL,
                                  tag_id BIGINT NOT NULL,
                                  PRIMARY KEY (translation_id, tag_id),
                                  FOREIGN KEY (translation_id) REFERENCES translations(id) ON DELETE CASCADE,
                                  FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
                                  INDEX idx_translation_id (translation_id),
                                  INDEX idx_tag_id (tag_id)
);
--rollback DROP TABLE translation_tags;

--changeset author:insert-default-locales
INSERT INTO locales (code, name) VALUES ('en', 'English');
INSERT INTO locales (code, name) VALUES ('fr', 'French');
INSERT INTO locales (code, name) VALUES ('es', 'Spanish');
--rollback DELETE FROM locales WHERE code IN ('en', 'fr', 'es');

--changeset author:insert-default-tags
INSERT INTO tags (name) VALUES ('mobile');
INSERT INTO tags (name) VALUES ('desktop');
INSERT INTO tags (name) VALUES ('web');
--rollback DELETE FROM tags WHERE name IN ('mobile', 'desktop', 'web');