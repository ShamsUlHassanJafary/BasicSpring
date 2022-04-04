package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.GarageAdmin;
import gms4u.repository.GarageAdminRepository;

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
 * Integration tests for the {@link GarageAdminResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class GarageAdminResourceIT {

    @Autowired
    private GarageAdminRepository garageAdminRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGarageAdminMockMvc;

    private GarageAdmin garageAdmin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GarageAdmin createEntity(EntityManager em) {
        GarageAdmin garageAdmin = new GarageAdmin();
        return garageAdmin;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GarageAdmin createUpdatedEntity(EntityManager em) {
        GarageAdmin garageAdmin = new GarageAdmin();
        return garageAdmin;
    }

    @BeforeEach
    public void initTest() {
        garageAdmin = createEntity(em);
    }

    @Test
    @Transactional
    public void createGarageAdmin() throws Exception {
        int databaseSizeBeforeCreate = garageAdminRepository.findAll().size();
        // Create the GarageAdmin
        restGarageAdminMockMvc.perform(post("/api/garage-admins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garageAdmin)))
            .andExpect(status().isCreated());

        // Validate the GarageAdmin in the database
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeCreate + 1);
        GarageAdmin testGarageAdmin = garageAdminList.get(garageAdminList.size() - 1);
    }

    @Test
    @Transactional
    public void createGarageAdminWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = garageAdminRepository.findAll().size();

        // Create the GarageAdmin with an existing ID
        garageAdmin.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGarageAdminMockMvc.perform(post("/api/garage-admins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garageAdmin)))
            .andExpect(status().isBadRequest());

        // Validate the GarageAdmin in the database
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllGarageAdmins() throws Exception {
        // Initialize the database
        garageAdminRepository.saveAndFlush(garageAdmin);

        // Get all the garageAdminList
        restGarageAdminMockMvc.perform(get("/api/garage-admins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(garageAdmin.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getGarageAdmin() throws Exception {
        // Initialize the database
        garageAdminRepository.saveAndFlush(garageAdmin);

        // Get the garageAdmin
        restGarageAdminMockMvc.perform(get("/api/garage-admins/{id}", garageAdmin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(garageAdmin.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingGarageAdmin() throws Exception {
        // Get the garageAdmin
        restGarageAdminMockMvc.perform(get("/api/garage-admins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGarageAdmin() throws Exception {
        // Initialize the database
        garageAdminRepository.saveAndFlush(garageAdmin);

        int databaseSizeBeforeUpdate = garageAdminRepository.findAll().size();

        // Update the garageAdmin
        GarageAdmin updatedGarageAdmin = garageAdminRepository.findById(garageAdmin.getId()).get();
        // Disconnect from session so that the updates on updatedGarageAdmin are not directly saved in db
        em.detach(updatedGarageAdmin);

        restGarageAdminMockMvc.perform(put("/api/garage-admins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedGarageAdmin)))
            .andExpect(status().isOk());

        // Validate the GarageAdmin in the database
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeUpdate);
        GarageAdmin testGarageAdmin = garageAdminList.get(garageAdminList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingGarageAdmin() throws Exception {
        int databaseSizeBeforeUpdate = garageAdminRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGarageAdminMockMvc.perform(put("/api/garage-admins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garageAdmin)))
            .andExpect(status().isBadRequest());

        // Validate the GarageAdmin in the database
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGarageAdmin() throws Exception {
        // Initialize the database
        garageAdminRepository.saveAndFlush(garageAdmin);

        int databaseSizeBeforeDelete = garageAdminRepository.findAll().size();

        // Delete the garageAdmin
        restGarageAdminMockMvc.perform(delete("/api/garage-admins/{id}", garageAdmin.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
