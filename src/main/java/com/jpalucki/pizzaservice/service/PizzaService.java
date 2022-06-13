package com.jpalucki.pizzaservice.service;

import com.jpalucki.pizzaservice.config.exception.*;
import com.jpalucki.pizzaservice.service.dto.*;

import java.util.*;

public interface PizzaService {

    Collection<PizzaDTO> getPizzas();

    PizzaDTO createPizza(PizzaDTO pizzaDTO) throws NotFoundException, ValidationException;

    void updatePizza(Long pizzaId, PizzaDTO pizzaDTO) throws NotFoundException, ValidationException;

    void deletePizza(Long pizzaId) throws NotFoundException;
}
