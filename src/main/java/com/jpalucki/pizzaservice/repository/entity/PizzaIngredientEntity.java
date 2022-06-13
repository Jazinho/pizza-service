package com.jpalucki.pizzaservice.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pizza_ingredient")
public class PizzaIngredientEntity {

    @EmbeddedId
    private PizzaIngredientKey pizzaIngredientKey;
}