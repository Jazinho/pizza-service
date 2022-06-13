package com.jpalucki.pizzaservice.service;

import com.jpalucki.pizzaservice.service.dto.*;

import java.util.*;

public interface PizzaService {

    Collection<PizzaDTO> getPizzas();

    PizzaDTO createPizza(PizzaDTO pizzaDTO) throws Exception;

    void updatePizza(Long pizzaId, PizzaDTO pizzaDTO) throws Exception;

    void deletePizza(Long pizzaId);
}
