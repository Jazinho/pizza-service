package com.jpalucki.pizzaservice.service;

import com.jpalucki.pizzaservice.service.dto.*;

import java.util.*;

public interface PizzaService {

    Collection<PizzaDTO> getPizzas();

    PizzaDTO createPizza(PizzaDTO pizzaDTO);

    void updatePizza(PizzaDTO pizzaDTO);

    void deletePizza(Long pizzaId);
}
