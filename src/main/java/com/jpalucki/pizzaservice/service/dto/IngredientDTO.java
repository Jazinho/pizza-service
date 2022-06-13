package com.jpalucki.pizzaservice.service.dto;

import com.jpalucki.pizzaservice.repository.entity.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Long id;
    private String name;

    public IngredientDTO fromEntity(IngredientEntity ingredientEntity) {
        setId(ingredientEntity.getId());
        setName(ingredientEntity.getName());
        return this;
    }
}
