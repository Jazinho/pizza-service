package com.jpalucki.pizzaservice.repository.entity;

import javax.persistence.*;
import java.util.*;

@Entity
public class PizzaEntity {

    @Id
    private Integer id;

    private String name;

    private Integer size;

    @ManyToMany
    @JoinTable(
        name = "pizza_ingredient",
        joinColumns = @JoinColumn(name = "pizza_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredients;
}
