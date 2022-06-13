package com.jpalucki.pizzaservice.service;

import com.jpalucki.pizzaservice.config.exception.*;
import com.jpalucki.pizzaservice.repository.*;
import com.jpalucki.pizzaservice.repository.entity.*;
import com.jpalucki.pizzaservice.service.dto.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.util.*;
import java.util.stream.*;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;
    private final IngredientRepository ingredientRepository;
    private final PizzaIngredientRepository pizzaIngredientRepository;

    public PizzaServiceImpl(
        PizzaRepository pizzaRepository,
        IngredientRepository ingredientRepository,
        PizzaIngredientRepository pizzaIngredientRepository
    ) {
        this.pizzaRepository = pizzaRepository;
        this.ingredientRepository = ingredientRepository;
        this.pizzaIngredientRepository = pizzaIngredientRepository;
    }

    @Override
    public Collection<PizzaDTO> getPizzas() {
        return pizzaRepository.findAll().stream()
            .map(pizzaEntity -> new PizzaDTO().fromEntity(pizzaEntity))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PizzaDTO createPizza(PizzaDTO pizzaDTO) throws Exception {
        validatePizzaData(pizzaDTO);
        List<IngredientEntity> ingredientEntities = pizzaDTO.getIngredients().stream()
            .map(ingredientDTO -> new IngredientEntity(ingredientDTO.getId(), ingredientDTO.getName()))
            .collect(Collectors.toList());
        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setName(pizzaDTO.getName());
        pizzaEntity.setSize(pizzaDTO.getSize());
        pizzaEntity.setIngredients(ingredientEntities);
        ingredientRepository.saveAll(ingredientEntities);
        return new PizzaDTO().fromEntity(pizzaRepository.save(pizzaEntity));
    }

    @Override
    @Transactional
    public void updatePizza(Long pizzaId, PizzaDTO pizzaDTO) throws Exception {
        validatePizzaData(pizzaDTO);
        PizzaEntity pizzaEntity = pizzaRepository.findById(pizzaId)
            .orElseThrow(() -> new NotFoundException("Pizza with ID=" + pizzaId + " not found in Database"));
        pizzaEntity.setName(pizzaDTO.getName());
        pizzaEntity.setSize(pizzaDTO.getSize());

        List<IngredientEntity> ingredientEntities = pizzaDTO.getIngredients().stream()
            .map(ingredientDTO -> new IngredientEntity(ingredientDTO.getId(), ingredientDTO.getName()))
            .collect(Collectors.toList());
        pizzaEntity.setIngredients(ingredientEntities);
        pizzaRepository.save(pizzaEntity);
        ingredientRepository.saveAll(ingredientEntities);
    }

    @Override
    @Transactional
    public void deletePizza(Long pizzaId) {
        pizzaIngredientRepository.deleteAllByPizzaId(pizzaId);
        pizzaRepository.deleteById(pizzaId);
    }

    private void validatePizzaData(PizzaDTO pizzaDto) throws Exception {
        if (pizzaDto.getIngredients().size() == 0)
            throw new ValidationException("Invalid data - pizza must have at least 1 ingredient");
        if (pizzaDto.getSize() <= 0)
            throw new ValidationException("Invalid data - pizza size must have positive number size");
        if (pizzaDto.getName().isEmpty())
            throw new ValidationException("Invalid data - pizza name must have at least length of 1 characters");
    }
}
