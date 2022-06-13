package com.jpalucki.pizzaservice.service.dto;

import com.jpalucki.pizzaservice.repository.entity.*;
import lombok.*;

import java.util.*;
import java.util.stream.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDTO {
    private Long id;
    private String name;
    private Integer size;
    private List<IngredientDTO> ingredients;

    public PizzaDTO fromEntity(PizzaEntity pizzaEntity) {
        setId(pizzaEntity.getId());
        setName(pizzaEntity.getName());
        setSize(pizzaEntity.getSize());
        setIngredients(
            pizzaEntity.getIngredients().stream()
                .map(i -> new IngredientDTO().fromEntity(i))
                .collect(Collectors.toList())
        );
        return this;
    }
}
