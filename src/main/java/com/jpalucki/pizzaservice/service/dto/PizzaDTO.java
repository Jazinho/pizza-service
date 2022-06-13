package com.jpalucki.pizzaservice.service.dto;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDTO {
    private Long id;
    private String name;
    private Integer size;
    private List<IngredientDTO> ingredients;
}
