package com.jpalucki.pizzaservice.repository;

import com.jpalucki.pizzaservice.repository.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;

public interface PizzaIngredientRepository extends CrudRepository<PizzaIngredientEntity, PizzaIngredientKey> {

    @Modifying
    @Query(value = "delete from pizza_ingredient where pizza_id = ?1", nativeQuery = true)
    void deleteAllByPizzaId(Long pizzaId);
}
