# pizza-service
Pizza Service as an Interview task.

Service exposes 4 simple endpoints of basic CRUD operations.

After starting application locally please head up to this
[link](http://localhost:8080/api/v1/swagger-ui/index.html?configUrl=/api/v1/v3/api-docs/swagger-config#).

# DB

Database consists of 3 tables:
- pizza - list of pizzas (ID, name, size)
- ingredient (ID, name)
- pizza_ingredient (pizza_id, ingredient_id)
