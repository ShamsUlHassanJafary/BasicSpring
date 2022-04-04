package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.Garage;
import gms4u.domain.GarageAdmin;
import gms4u.domain.GarageType;
import gms4u.repository.GarageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link GarageResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class GarageResourceIT {

    private static final String DEFAULT_BUSINESS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LINE_ADDRESS_1 = "AAAAAAAAAA";
    private static final String UPDATED_LINE_ADDRESS_1 = "BBBBBBBBBB";

    private static final String DEFAULT_LINE_ADDRESS_2 = "AAAAAAAAAA";
    private static final String UPDATED_LINE_ADDRESS_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTY = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    @Autowired
    private GarageRepository garageRepository;

    @Mock
    private GarageRepository garageRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGarageMockMvc;

    private Garage garage;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Garage createEntity(EntityManager em) {
        Garage garage = new Garage()
            .businessName(DEFAULT_BUSINESS_NAME)
            .lineAddress1(DEFAULT_LINE_ADDRESS_1)
            .lineAddress2(DEFAULT_LINE_ADDRESS_2)
            .city(DEFAULT_CITY)
            .county(DEFAULT_COUNTY)
            .postcode(DEFAULT_POSTCODE)
            .country(DEFAULT_COUNTRY).phoneNumber(DEFAULT_PHONE_NUMBER);



        // Add required entity
        GarageAdmin garageAdmin;
        if (TestUtil.findAll(em, GarageAdmin.class).isEmpty()) {
            garageAdmin = GarageAdminResourceIT.createEntity(em);
            em.persist(garageAdmin);
            em.flush();
        } else {
            garageAdmin = TestUtil.findAll(em, GarageAdmin.class).get(0);
        }
        garage.getGarageAdmins().add(garageAdmin);

        GarageType garageType;
        if (TestUtil.findAll(em, GarageType.class).isEmpty()) {
            garageType = GarageTypeResourceIT.createEntity(em);
            em.persist(garageType);
            em.flush();
        } else {
            garageType = TestUtil.findAll(em, GarageType.class).get(0);
        }
        garage.setGarageType(garageType);

        return garage;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Garage createUpdatedEntity(EntityManager em) {
        Garage garage = new Garage()
            .businessName(UPDATED_BUSINESS_NAME)
            .lineAddress1(UPDATED_LINE_ADDRESS_1)
            .lineAddress2(UPDATED_LINE_ADDRESS_2)
            .city(UPDATED_CITY)
            .county(UPDATED_COUNTY)
            .postcode(UPDATED_POSTCODE)
            .country(UPDATED_COUNTRY);

        // Add required entity
        GarageAdmin garageAdmin;
        if (TestUtil.findAll(em, GarageAdmin.class).isEmpty()) {
            garageAdmin = GarageAdminResourceIT.createUpdatedEntity(em);
            em.persist(garageAdmin);
            em.flush();
        } else {
            garageAdmin = TestUtil.findAll(em, GarageAdmin.class).get(0);
        }
        garage.getGarageAdmins().add(garageAdmin);

        GarageType garageType;
        if (TestUtil.findAll(em, GarageType.class).isEmpty()) {
            garageType = GarageTypeResourceIT.createUpdatedEntity(em);
            em.persist(garageType);
            em.flush();
        } else {
            garageType = TestUtil.findAll(em, GarageType.class).get(0);
        }
        garage.setGarageType(garageType);

        return garage;
    }

    @BeforeEach
    public void initTest() {
        garage = createEntity(em);
    }

    @Test
    @Transactional
    public void createGarage() throws Exception {
        int databaseSizeBeforeCreate = garageRepository.findAll().size();
        // Create the Garage
        restGarageMockMvc.perform(post("/api/garages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isCreated());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeCreate + 1);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getBusinessName()).isEqualTo(DEFAULT_BUSINESS_NAME);
        assertThat(testGarage.getLineAddress1()).isEqualTo(DEFAULT_LINE_ADDRESS_1);
        assertThat(testGarage.getLineAddress2()).isEqualTo(DEFAULT_LINE_ADDRESS_2);
        assertThat(testGarage.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testGarage.getCounty()).isEqualTo(DEFAULT_COUNTY);
        assertThat(testGarage.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testGarage.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    public void createGarageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = garageRepository.findAll().size();

        // Create the Garage with an existing ID
        garage.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGarageMockMvc.perform(post("/api/garages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllGarages() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        // Get all the garageList
        restGarageMockMvc.perform(get("/api/garages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(garage.getId().intValue())))
            .andExpect(jsonPath("$.[*].businessName").value(hasItem(DEFAULT_BUSINESS_NAME)))
            .andExpect(jsonPath("$.[*].lineAddress1").value(hasItem(DEFAULT_LINE_ADDRESS_1)))
            .andExpect(jsonPath("$.[*].lineAddress2").value(hasItem(DEFAULT_LINE_ADDRESS_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].county").value(hasItem(DEFAULT_COUNTY)))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllGaragesWithEagerRelationshipsIsEnabled() throws Exception {
        when(garageRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGarageMockMvc.perform(get("/api/garages?eagerload=true"))
            .andExpect(status().isOk());

        verify(garageRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllGaragesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(garageRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGarageMockMvc.perform(get("/api/garages?eagerload=true"))
            .andExpect(status().isOk());

        verify(garageRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        // Get the garage
        restGarageMockMvc.perform(get("/api/garages/{id}", garage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(garage.getId().intValue()))
            .andExpect(jsonPath("$.businessName").value(DEFAULT_BUSINESS_NAME))
            .andExpect(jsonPath("$.lineAddress1").value(DEFAULT_LINE_ADDRESS_1))
            .andExpect(jsonPath("$.lineAddress2").value(DEFAULT_LINE_ADDRESS_2))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.county").value(DEFAULT_COUNTY))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    public void getNonExistingGarage() throws Exception {
        // Get the garage
        restGarageMockMvc.perform(get("/api/garages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeUpdate = garageRepository.findAll().size();

        // Update the garage
        Garage updatedGarage = garageRepository.findById(garage.getId()).get();
        // Disconnect from session so that the updates on updatedGarage are not directly saved in db
        em.detach(updatedGarage);
        updatedGarage
            .businessName(UPDATED_BUSINESS_NAME)
            .lineAddress1(UPDATED_LINE_ADDRESS_1)
            .lineAddress2(UPDATED_LINE_ADDRESS_2)
            .city(UPDATED_CITY)
            .county(UPDATED_COUNTY)
            .postcode(UPDATED_POSTCODE)
            .country(UPDATED_COUNTRY);

        restGarageMockMvc.perform(put("/api/garages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedGarage)))
            .andExpect(status().isOk());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getBusinessName()).isEqualTo(UPDATED_BUSINESS_NAME);
        assertThat(testGarage.getLineAddress1()).isEqualTo(UPDATED_LINE_ADDRESS_1);
        assertThat(testGarage.getLineAddress2()).isEqualTo(UPDATED_LINE_ADDRESS_2);
        assertThat(testGarage.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testGarage.getCounty()).isEqualTo(UPDATED_COUNTY);
        assertThat(testGarage.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testGarage.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void updateNonExistingGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGarageMockMvc.perform(put("/api/garages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeDelete = garageRepository.findAll().size();

        // Delete the garage
        restGarageMockMvc.perform(delete("/api/garages/{id}", garage.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    //    @Test
    @Transactional
    public void uploadGarageLogo() throws Exception {

        File resource = ResourceUtils.getFile("src/main/webapp/content/images/logo/teknikality-logo.png");
        assertThat(resource).isNotNull();


        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.write(Files.readAllBytes(resource.toPath()));
            MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "teknikality-logo.png",
                MediaType.IMAGE_JPEG_VALUE,
                byteArrayOutputStream.toByteArray()
            );


            // Delete the garage
            restGarageMockMvc.perform(multipart("/api/garages/{id}/upload-logo", garage.getId()).file(file))
                .andExpect(status().isOk());
        }


    }
}
