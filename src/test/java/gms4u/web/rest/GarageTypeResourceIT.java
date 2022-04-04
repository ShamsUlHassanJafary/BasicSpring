package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.GarageType;
import gms4u.repository.GarageTypeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link GarageTypeResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class GarageTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private GarageTypeRepository garageTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGarageTypeMockMvc;

    private GarageType garageType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GarageType createEntity(EntityManager em) {
        GarageType garageType = new GarageType()
            .name(DEFAULT_NAME);
        return garageType;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GarageType createUpdatedEntity(EntityManager em) {
        GarageType garageType = new GarageType()
            .name(UPDATED_NAME);
        return garageType;
    }

    @BeforeEach
    public void initTest() {
        garageType = createEntity(em);
    }

    @Test
    @Transactional
    public void getAllGarageTypes() throws Exception {
        // Initialize the database
        garageTypeRepository.saveAndFlush(garageType);

        // Get all the garageTypeList
        restGarageTypeMockMvc.perform(get("/api/garage-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(garageType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getGarageType() throws Exception {
        // Initialize the database
        garageTypeRepository.saveAndFlush(garageType);

        // Get the garageType
        restGarageTypeMockMvc.perform(get("/api/garage-types/{id}", garageType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(garageType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingGarageType() throws Exception {
        // Get the garageType
        restGarageTypeMockMvc.perform(get("/api/garage-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}
