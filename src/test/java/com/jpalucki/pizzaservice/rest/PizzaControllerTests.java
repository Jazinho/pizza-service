package com.jpalucki.pizzaservice.rest;

import com.fasterxml.jackson.core.type.*;
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

import static org.junit.jupiter.api.Assertions.*;

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

        List<PizzaDTO> pizzaDTOs = mapper.readValue(result, new TypeReference<List<PizzaDTO>>() {
        });
        assertEquals(pizzaDTOs.size(), 2);
        assertEquals(pizzaDTOs.get(0).getName(), createdPizzas.get(0).getName());
        assertEquals(pizzaDTOs.get(0).getSize(), createdPizzas.get(0).getSize());
        assertEquals(pizzaDTOs.get(0).getIngredients().size(), 2);

        assertEquals(pizzaDTOs.get(1).getName(), createdPizzas.get(1).getName());
        assertEquals(pizzaDTOs.get(1).getSize(), createdPizzas.get(1).getSize());
        assertEquals(pizzaDTOs.get(1).getIngredients().size(), 3);
    }

    @Test
    public void createPizza_created() throws Exception {
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

        assertEquals(result, "Invalid data - pizza name must have at least length of 1 characters");
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
    public void createPizza_someNewIngredients() throws Exception {
        //given
        List<PizzaEntity> pizzas = setupDB();
        Long existingIngredientId = pizzas.get(0).getIngredients().get(0).getId();
        String existingIngredientName = pizzas.get(0).getIngredients().get(0).getName();
        PizzaDTO pizzaToBeCreated = new PizzaDTO(null, "Some name", 30, Lists.list(
            new IngredientDTO(existingIngredientId, existingIngredientName), new IngredientDTO(null, "Paprika")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/pizzas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn().getResponse().getContentAsString();

        List<IngredientEntity> ingredients = (List<IngredientEntity>) ingredientRepository.findAll();
        assertEquals(ingredients.size(), 4);
    }

    @Test
    public void updatePizza_existing() throws Exception {
        //given
        List<PizzaEntity> pizzas = setupDB();
        Long idToUpdate = pizzas.get(0).getId();
        Long existingIngredientId = pizzas.get(0).getIngredients().get(0).getId();
        String existingIngredientName = pizzas.get(0).getIngredients().get(0).getName();
        PizzaDTO pizzaToBeUpdated = new PizzaDTO(idToUpdate, "Updated Margharitta", 50,
            Lists.list(new IngredientDTO(existingIngredientId, existingIngredientName)));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeUpdated));

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        PizzaEntity updatedPizza = pizzaRepository.findById(idToUpdate).get();
        assertEquals(updatedPizza.getName(), "Updated Margharitta");
        assertEquals(updatedPizza.getSize(), 50);
        assertEquals(updatedPizza.getIngredients().size(), 1);
    }

    @Test
    public void updatePizza_notExisting() throws Exception {
        //given
        setupDB();
        PizzaDTO pizzaToBeUpdated = new PizzaDTO(null, "Updated Margharitta", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeUpdated));

        // then
        String response = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn().getResponse().getContentAsString();
        assertEquals(response, "Pizza with ID=1 not found in Database");
    }

    @Test
    public void updatePizza_invalidName() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(1L, "", 50, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza name must have at least length of 1 characters");
    }

    @Test
    public void updatePizza_invalidSize() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(1L, "Some name", 0, Lists.list(new IngredientDTO(1L, "Cheese")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza size must have positive number size");
    }

    @Test
    public void updatePizza_invalidIngredients() throws Exception {
        //given
        PizzaDTO pizzaToBeCreated = new PizzaDTO(1L, "Some name", 40, Lists.list());

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeCreated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertEquals(result, "Invalid data - pizza must have at least 1 ingredient");
    }

    @Test
    public void updatePizza_someNewIngredients() throws Exception {
        //given
        List<PizzaEntity> pizzas = setupDB();
        Long idToUpdate = pizzas.get(0).getId();
        Long existingIngredientId = pizzas.get(0).getIngredients().get(0).getId();
        String existingIngredientName = pizzas.get(0).getIngredients().get(0).getName();
        PizzaDTO pizzaToBeUpdated = new PizzaDTO(idToUpdate, "Some name", 30, Lists.list(
            new IngredientDTO(existingIngredientId, existingIngredientName), new IngredientDTO(null, "Paprika")));

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pizzas/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pizzaToBeUpdated));

        // then
        String result = mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        List<IngredientEntity> ingredients = (List<IngredientEntity>) ingredientRepository.findAll();
        assertEquals(ingredients.size(), 4);
    }

    @Test
    public void deletePizza_ok() throws Exception {
        //given
        List<PizzaEntity> pizzas = setupDB();
        Long idToDelete = pizzas.get(0).getId();

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/pizzas/" + idToDelete);

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        assertFalse(pizzaRepository.existsById(idToDelete));
        assertEquals(pizzaIngredientRepository.findAllByPizzaId(idToDelete).size(), 0);
    }

    @Test
    public void deletePizza_nonExisting() throws Exception {
        //given
        Long idToDelete = 1L;

        // when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/pizzas/" + idToDelete);

        // then
        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        assertFalse(pizzaRepository.existsById(idToDelete));
    }

    private List<PizzaEntity> setupDB() {
        pizzaIngredientRepository.deleteAll();
        ingredientRepository.deleteAll();
        pizzaRepository.deleteAll();

        IngredientEntity cheese = new IngredientEntity(null, "Cheese");
        IngredientEntity herbs = new IngredientEntity(null, "Herbs");
        IngredientEntity mushrooms = new IngredientEntity(null, "Mushrooms");
        List<IngredientEntity> ingredientEntities = (List) ingredientRepository.saveAll(Lists.list(cheese, herbs, mushrooms));

        String name1 = "Margheritta";
        String name2 = "Funghi";
        Integer size1 = 40;
        Integer size2 = 30;
        PizzaEntity pizza1 = new PizzaEntity(null, name1, size1, Lists.list(cheese, herbs));
        PizzaEntity pizza2 = new PizzaEntity(null, name2, size2, Lists.list(cheese, herbs, mushrooms));
        List<PizzaEntity> pizzas = (List) pizzaRepository.saveAll(Lists.list(pizza1, pizza2));
        Long i1 = ingredientEntities.get(0).getId();
        Long i2 = ingredientEntities.get(1).getId();
        Long i3 = ingredientEntities.get(2).getId();
        Long pizza1Id = pizzas.get(0).getId();
        Long pizza2Id = pizzas.get(1).getId();

        PizzaIngredientEntity p1i1 = new PizzaIngredientEntity(new PizzaIngredientKey(pizza1Id, i1));
        PizzaIngredientEntity p1i2 = new PizzaIngredientEntity(new PizzaIngredientKey(pizza1Id, i2));
        PizzaIngredientEntity p2i1 = new PizzaIngredientEntity(new PizzaIngredientKey(pizza2Id, i1));
        PizzaIngredientEntity p2i2 = new PizzaIngredientEntity(new PizzaIngredientKey(pizza2Id, i2));
        PizzaIngredientEntity p2i3 = new PizzaIngredientEntity(new PizzaIngredientKey(pizza2Id, i3));
        pizzaIngredientRepository.saveAll(Lists.list(p1i1, p1i2, p2i1, p2i2, p2i3));

        return pizzas;
    }
}
