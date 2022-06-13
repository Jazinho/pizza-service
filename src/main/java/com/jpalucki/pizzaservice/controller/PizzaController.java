package com.jpalucki.pizzaservice.controller;

import com.jpalucki.pizzaservice.service.*;
import com.jpalucki.pizzaservice.service.dto.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.*;

import javax.servlet.http.*;
import java.util.*;

@RestController("pizzas")
public class PizzaController {

    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService){
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public Collection<PizzaDTO> getPizzas() {
        return pizzaService.getPizzas();
    }

    @PostMapping
    public PizzaDTO createPizza(@RequestBody PizzaDTO pizzaDTO, HttpServletResponse response) {
        PizzaDTO createdPizza = pizzaService.createPizza(pizzaDTO);
        String uri = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("pizzas/" + createdPizza.getId())
            .build()
            .toUriString();
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", uri);
        return createdPizza;
    }

    @PutMapping
    public void updatePizza(@RequestBody PizzaDTO pizzaDTO, HttpServletResponse response) {
        pizzaService.updatePizza(pizzaDTO);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @DeleteMapping("/{pizza_id}")
    public void deletePizza(@PathVariable Long pizzaId, HttpServletResponse response) {
        pizzaService.deletePizza(pizzaId);
    }
}
