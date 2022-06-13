package com.jpalucki.pizzaservice.controller;

import com.jpalucki.pizzaservice.config.exception.*;
import com.jpalucki.pizzaservice.service.*;
import com.jpalucki.pizzaservice.service.dto.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.*;

import javax.servlet.http.*;
import java.util.*;

@Tag(name = "PizzaService API - Pizzas Controller")
@RestController
@RequestMapping("pizzas")
public class PizzaController {

    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @Operation(summary = "Get all pizzas.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "OK.")
    )
    @GetMapping
    public Collection<PizzaDTO> getPizzas() {
        return pizzaService.getPizzas();
    }

    @Operation(summary = "Create new pizza.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK."),
        @ApiResponse(responseCode = "400", description = "Bad Request."),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity.")
    })
    @PostMapping
    public PizzaDTO createPizza(@RequestBody PizzaDTO pizzaDTO, HttpServletResponse response)
        throws NotFoundException, ValidationException {
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

    @Operation(summary = "Update pizza.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK."),
        @ApiResponse(responseCode = "400", description = "Bad Request."),
        @ApiResponse(responseCode = "404", description = "Not found."),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity.")
    })
    @PutMapping("/{pizzaId}")
    public void updatePizza(@PathVariable Long pizzaId, @RequestBody PizzaDTO pizzaDTO, HttpServletResponse response)
        throws NotFoundException, ValidationException {
        pizzaService.updatePizza(pizzaId, pizzaDTO);
    }

    @Operation(summary = "Delete pizza.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK."),
        @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @DeleteMapping("/{pizzaId}")
    public void deletePizza(@PathVariable Long pizzaId, HttpServletResponse response) throws NotFoundException {
        pizzaService.deletePizza(pizzaId);
    }
}
