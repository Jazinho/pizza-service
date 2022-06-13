package com.jpalucki.pizzaservice.repository;

import com.jpalucki.pizzaservice.repository.entity.*;
import org.springframework.data.repository.*;

public interface PizzaIngredientRepository extends CrudRepository<PizzaIngredientEntity, PizzaIngredientKey> {
}
