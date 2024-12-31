CREATE TABLE category(
    id VARCHAR(26),
    name VARCHAR(50),
    CONSTRAINT category_pk PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX name_un_idx ON category(name);

COMMENT ON TABLE category is 'Category table';
COMMENT ON COLUMN category.id is 'Category id';
COMMENT ON COLUMN category.name is 'Category name';