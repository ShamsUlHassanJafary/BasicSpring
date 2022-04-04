package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.GarageAdmin;
import gms4u.repository.GarageAdminRepository;
import gms4u.repository.GarageRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.UserService;
import gms4u.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static gms4u.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the GarageAdminResource REST controller.
 *
 * @see GarageAdminResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Gms4UApp.class)
public class GarageAdminResourceIntTest {

    @Autowired
    private GarageAdminRepository garageAdminRepository;
    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private GarageAdminService garageAdminService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restGarageAdminMockMvc;

    private GarageAdmin garageAdmin;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GarageAdmin createEntity(EntityManager em) {
        GarageAdmin garageAdmin = new GarageAdmin();
        return garageAdmin;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GarageAdminResource garageAdminResource = new GarageAdminResource(garageAdminRepository, garageRepository, userService, garageAdminService);
        this.restGarageAdminMockMvc = MockMvcBuilders.standaloneSetup(garageAdminResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    @Before
    public void initTest() {
        garageAdmin = createEntity(em);
    }

    @Test
    @Transactional
    public void createGarageAdmin() throws Exception {
        int databaseSizeBeforeCreate = garageAdminRepository.findAll().size();

        // Create the GarageAdmin
        restGarageAdminMockMvc.perform(post("/api/garage-admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
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
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
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
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
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

        // Create the GarageAdmin

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGarageAdminMockMvc.perform(put("/api/garage-admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
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
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<GarageAdmin> garageAdminList = garageAdminRepository.findAll();
        assertThat(garageAdminList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GarageAdmin.class);
        GarageAdmin garageAdmin1 = new GarageAdmin();
        garageAdmin1.setId(1L);
        GarageAdmin garageAdmin2 = new GarageAdmin();
        garageAdmin2.setId(garageAdmin1.getId());
        assertThat(garageAdmin1).isEqualTo(garageAdmin2);
        garageAdmin2.setId(2L);
        assertThat(garageAdmin1).isNotEqualTo(garageAdmin2);
        garageAdmin1.setId(null);
        assertThat(garageAdmin1).isNotEqualTo(garageAdmin2);
    }
}
