CREATE TABLE IF NOT EXISTS notes (
    id SERIAL,
    title VARCHAR(32) NOT NULL,
    description VARCHAR(32) NOT NULL,
    slug VARCHAR(32) NOT NULL,
    menu_label VARCHAR(32) NOT NULL,
    h1 VARCHAR(32) NOT NULL,
    published_at DATE NOT NULL,
    priority INT NOT NULL,
    PRIMARY KEY(id)
);
