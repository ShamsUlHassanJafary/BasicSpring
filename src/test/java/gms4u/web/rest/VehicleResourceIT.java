package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.Vehicle;
import gms4u.domain.Garage;
import gms4u.repository.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link VehicleResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class VehicleResourceIT {

    private static final String DEFAULT_REGISTRATION = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION = "BBBBBBBBBB";

    private static final String DEFAULT_MAKE = "AAAAAAAAAA";
    private static final String UPDATED_MAKE = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_COLOUR = "AAAAAAAAAA";
    private static final String UPDATED_COLOUR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_MOT_DUE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MOT_DUE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleRepository vehicleRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVehicleMockMvc;

    private Vehicle vehicle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehicle createEntity(EntityManager em) {
        Vehicle vehicle = new Vehicle()
            .registration(DEFAULT_REGISTRATION)
            .make(DEFAULT_MAKE)
            .model(DEFAULT_MODEL)
            .colour(DEFAULT_COLOUR)
            .motExpiryDate(DEFAULT_MOT_DUE);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        vehicle.getGarages().add(garage);
        return vehicle;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehicle createUpdatedEntity(EntityManager em) {
        Vehicle vehicle = new Vehicle()
            .registration(UPDATED_REGISTRATION)
            .make(UPDATED_MAKE)
            .model(UPDATED_MODEL)
            .colour(UPDATED_COLOUR)
            .motExpiryDate(UPDATED_MOT_DUE);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createUpdatedEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        vehicle.getGarages().add(garage);
        return vehicle;
    }

    @BeforeEach
    public void initTest() {
        vehicle = createEntity(em);
    }

    @Test
    @Transactional
    public void createVehicle() throws Exception {
        int databaseSizeBeforeCreate = vehicleRepository.findAll().size();
        // Create the Vehicle
        restVehicleMockMvc.perform(post("/api/vehicles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vehicle)))
            .andExpect(status().isCreated());

        // Validate the Vehicle in the database
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        assertThat(vehicleList).hasSize(databaseSizeBeforeCreate + 1);
        Vehicle testVehicle = vehicleList.get(vehicleList.size() - 1);
        assertThat(testVehicle.getRegistration()).isEqualTo(DEFAULT_REGISTRATION);
        assertThat(testVehicle.getMake()).isEqualTo(DEFAULT_MAKE);
        assertThat(testVehicle.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testVehicle.getColour()).isEqualTo(DEFAULT_COLOUR);
        assertThat(testVehicle.getMotExpiryDate()).isEqualTo(DEFAULT_MOT_DUE);
    }

    @Test
    @Transactional
    public void createVehicleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vehicleRepository.findAll().size();

        // Create the Vehicle with an existing ID
        vehicle.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVehicleMockMvc.perform(post("/api/vehicles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vehicle)))
            .andExpect(status().isBadRequest());

        // Validate the Vehicle in the database
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        assertThat(vehicleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllVehicles() throws Exception {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle);

        // Get all the vehicleList
        restVehicleMockMvc.perform(get("/api/vehicles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicle.getId().intValue())))
            .andExpect(jsonPath("$.[*].registration").value(hasItem(DEFAULT_REGISTRATION)))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].colour").value(hasItem(DEFAULT_COLOUR)))
            .andExpect(jsonPath("$.[*].motExpiryDate").value(hasItem(DEFAULT_MOT_DUE.toString())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllVehiclesWithEagerRelationshipsIsEnabled() throws Exception {
        when(vehicleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVehicleMockMvc.perform(get("/api/vehicles?eagerload=true"))
            .andExpect(status().isOk());

        verify(vehicleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllVehiclesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(vehicleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVehicleMockMvc.perform(get("/api/vehicles?eagerload=true"))
            .andExpect(status().isOk());

        verify(vehicleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getVehicle() throws Exception {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle);

        // Get the vehicle
        restVehicleMockMvc.perform(get("/api/vehicles/{id}", vehicle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vehicle.getId().intValue()))
            .andExpect(jsonPath("$.registration").value(DEFAULT_REGISTRATION))
            .andExpect(jsonPath("$.make").value(DEFAULT_MAKE))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.colour").value(DEFAULT_COLOUR))
            .andExpect(jsonPath("$.motExpiryDate").value(DEFAULT_MOT_DUE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingVehicle() throws Exception {
        // Get the vehicle
        restVehicleMockMvc.perform(get("/api/vehicles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVehicle() throws Exception {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle);

        int databaseSizeBeforeUpdate = vehicleRepository.findAll().size();

        // Update the vehicle
        Vehicle updatedVehicle = vehicleRepository.findById(vehicle.getId()).get();
        // Disconnect from session so that the updates on updatedVehicle are not directly saved in db
        em.detach(updatedVehicle);
        updatedVehicle
            .registration(UPDATED_REGISTRATION)
            .make(UPDATED_MAKE)
            .model(UPDATED_MODEL)
            .colour(UPDATED_COLOUR)
            .motExpiryDate(UPDATED_MOT_DUE);

        restVehicleMockMvc.perform(put("/api/vehicles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedVehicle)))
            .andExpect(status().isOk());

        // Validate the Vehicle in the database
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate);
        Vehicle testVehicle = vehicleList.get(vehicleList.size() - 1);
        assertThat(testVehicle.getRegistration()).isEqualTo(UPDATED_REGISTRATION);
        assertThat(testVehicle.getMake()).isEqualTo(UPDATED_MAKE);
        assertThat(testVehicle.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testVehicle.getColour()).isEqualTo(UPDATED_COLOUR);
        assertThat(testVehicle.getMotExpiryDate()).isEqualTo(UPDATED_MOT_DUE);
    }

    @Test
    @Transactional
    public void updateNonExistingVehicle() throws Exception {
        int databaseSizeBeforeUpdate = vehicleRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(put("/api/vehicles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vehicle)))
            .andExpect(status().isBadRequest());

        // Validate the Vehicle in the database
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteVehicle() throws Exception {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle);

        int databaseSizeBeforeDelete = vehicleRepository.findAll().size();

        // Delete the vehicle
        restVehicleMockMvc.perform(delete("/api/vehicles/{id}", vehicle.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        assertThat(vehicleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
