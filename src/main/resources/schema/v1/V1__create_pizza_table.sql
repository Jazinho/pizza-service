CREATE TABLE pizza (
  id    SERIAL PRIMARY KEY,
  name  VARCHAR(64),
  size  SMALLINT
);

CREATE TABLE ingredient (
  id    SERIAL PRIMARY KEY,
  name  VARCHAR(64)
);

CREATE TABLE pizza_ingredient (
  pizza_id        SERIAL,
  ingredient_id   SERIAL,
  FOREIGN KEY (pizza_id) REFERENCES pizza(id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);