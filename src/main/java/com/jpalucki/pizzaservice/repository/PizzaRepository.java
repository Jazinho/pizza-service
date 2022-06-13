package com.jpalucki.pizzaservice.repository;

import com.jpalucki.pizzaservice.repository.entity.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface PizzaRepository extends CrudRepository<PizzaEntity, Long> {

    List<PizzaEntity> findAll();


}
