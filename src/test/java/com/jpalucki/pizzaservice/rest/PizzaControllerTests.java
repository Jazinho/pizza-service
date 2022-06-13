package com.jpalucki.pizzaservice.rest;

import com.fasterxml.jackson.databind.*;
import com.jpalucki.pizzaservice.*;
import com.jpalucki.pizzaservice.repository.*;
import com.jpalucki.pizzaservice.repository.entity.*;
import com.jpalucki.pizzaservice.service.dto.*;
import org.assertj.core.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.*;
import org.springframework.test.web.servlet.result.*;

import javax.transaction.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {PizzaServiceApplication.class},
    properties = {}
)
@ContextConfiguration
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PizzaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PizzaIngredientRepository pizzaIngredientRepository;

    @Test
    public void getNonePizzas() throws Exception {
        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/pizzas");

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        Collection<PizzaDTO> pizzaDTOs = mapper.readValue(result, Collection.class);
        assertEquals(pizzaDTOs.size(), 0);
    }

    @Test
    public void getSomePizzas() throws Exception {
        //given
        List<PizzaEntity> createdPizzas = (List<PizzaEntity>) setupDB();

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/pizzas");

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        List<PizzaDTO> pizzaDTOs = mapper.readValue(result, List.class);
        assertEquals(pizzaDTOs.size(), 2);
        assertEquals(pizzaDTOs.get(0).getName(), createdPizzas.get(0).getName());
        assertEquals(pizzaDTOs.get(0).getSize(), createdPizzas.get(0).getSize());
        assertEquals(pizzaDTOs.get(0).getIngredients().size(), 2);

        assertEquals(pizzaDTOs.get(1).getName(), createdPizzas.get(1).getName());
        assertEquals(pizzaDTOs.get(1).getSize(), createdPizzas.get(1).getSize());
        assertEquals(pizzaDTOs.get(1).getIngredients().size(), 3);
    }

    @Test
    public void createPizza() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(null, "New Pizza", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/pizzas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
            .andReturn().getResponse().getContentAsString();

        PizzaDTO pizzaDTOs = mapper.readValue(result, PizzaDTO.class);
        assertEquals(pizzaDTOs.getName(), "New Pizza");
        assertEquals(pizzaDTOs.getSize(), 50);
        assertEquals(pizzaDTOs.getIngredients().size(), 1);
        assertNotNull(pizzaDTOs.getId());
    }

    @Test
    public void createPizza_invalidName() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(null, "", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/pizzas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza name must have at least length of 4 characters");
    }

    @Test
    public void createPizza_invalidSize() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(null, "Some name", 0, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/pizzas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza size must have positive number size");
    }

    @Test
    public void createPizza_invalidIngredients() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(null, "Some name", 40, Lists.list());

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/pizzas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza must have at least 1 ingredient");
    }

    @Test
    public void updatePizza_existing() throws Exception {
        //given
        setupDB();
        PizzaDTO pizzaToBeUpdated = new PizzaDTO(1L, "Updated Margharitta", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeUpdated));

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        PizzaEntity updatedPizza = pizzaRepository.findById(1L).get();
        assertEquals(updatedPizza.getName(), "Updated Margharitta");
        assertEquals(updatedPizza.getSize(), 50);
        assertEquals(updatedPizza.getIngredients().size(), 1);
    }

    @Test
    public void updatePizza_notExisting() throws Exception {
        //given
        setupDB();
        PizzaDTO pizzaToBeUpdated = new PizzaDTO(5L, "Updated Margharitta", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeUpdated));

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        PizzaEntity updatedPizza = pizzaRepository.findById(1L).get();
        assertEquals(updatedPizza.getName(), "Updated Margharitta");
        assertEquals(updatedPizza.getSize(), 50);
        assertEquals(updatedPizza.getIngredients().size(), 1);
    }

    @Test
    public void deletePizza() throws Exception {
        //given
        setupDB();

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/pizzas/1");

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        assertFalse(pizzaRepository.existsById(1L));
    }

    private Iterable<PizzaEntity> setupDB(){
        IngredientEntity cheese = new IngredientEntity(1L, "Cheese");
        IngredientEntity herbs = new IngredientEntity(2L, "Herbs");
        IngredientEntity mushrooms = new IngredientEntity(3L, "Mushrooms");
        ingredientRepository.saveAll(Lists.list(cheese, herbs, mushrooms));

        String name1 = "Margheritta";
        String name2 = "Funghi";
        Integer size1 = 40;
        Integer size2 = 30;
        PizzaEntity pizza1 = new PizzaEntity(1L, name1, size1, Lists.list(cheese, herbs));
        PizzaEntity pizza2 = new PizzaEntity(2L, name2, size2, Lists.list(cheese, herbs, mushrooms));
        Iterable<PizzaEntity> pizzas = pizzaRepository.saveAll(Lists.list(pizza1, pizza2));

        PizzaIngredientEntity p1i1 = new PizzaIngredientEntity(new PizzaIngredientKey(1L, 1L));
        PizzaIngredientEntity p1i2 = new PizzaIngredientEntity(new PizzaIngredientKey(1L, 2L));
        PizzaIngredientEntity p2i1 = new PizzaIngredientEntity(new PizzaIngredientKey(2L, 1L));
        PizzaIngredientEntity p2i2 = new PizzaIngredientEntity(new PizzaIngredientKey(2L, 2L));
        PizzaIngredientEntity p2i3 = new PizzaIngredientEntity(new PizzaIngredientKey(2L, 3L));
        pizzaIngredientRepository.saveAll(Lists.list(p1i1, p1i2, p2i1, p2i2, p2i3));
        
        return pizzas;
    }
}
