package com.jpalucki.pizzaservice.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@Embeddable
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PizzaIngredientKey implements Serializable {

    @Column(name = "pizza_id")
    private Long pizzaId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}
