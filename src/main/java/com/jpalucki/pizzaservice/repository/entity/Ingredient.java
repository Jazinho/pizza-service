package com.jpalucki.pizzaservice.repository.entity;

import javax.persistence.*;

@Entity
public class Ingredient {

    @Id
    private Integer id;

    private String name;
}
