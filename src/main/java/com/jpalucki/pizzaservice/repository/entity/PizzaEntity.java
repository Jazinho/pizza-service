package com.jpalucki.pizzaservice.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pizza")
public class PizzaEntity {

    @Id
    private Long id;

    private String name;

    private Integer size;

    @ManyToMany
    @JoinTable(
        name = "pizza_ingredient",
        joinColumns = @JoinColumn(name = "pizza_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<IngredientEntity> ingredients;
}
