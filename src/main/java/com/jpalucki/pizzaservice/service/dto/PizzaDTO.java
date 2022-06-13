package com.jpalucki.pizzaservice.service.dto;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
public class PizzaDTO {
    private Long id;
    private String name;
    private Integer size;
    private List<String> ingredients;
}
