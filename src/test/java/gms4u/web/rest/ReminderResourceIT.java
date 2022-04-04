package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.Reminder;
import gms4u.domain.Customer;
import gms4u.domain.Vehicle;
import gms4u.domain.Garage;
import gms4u.repository.ReminderRepository;

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
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static gms4u.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ReminderResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ReminderResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EVENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EVENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final LocalDate DEFAULT_ALERT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ALERT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SECOND_ALERT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SECOND_ALERT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_EMAIL_ENABLED = false;
    private static final Boolean UPDATED_EMAIL_ENABLED = true;

    private static final Boolean DEFAULT_SMS_ENABLED = false;
    private static final Boolean UPDATED_SMS_ENABLED = true;

    private static final ZonedDateTime DEFAULT_ALERT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ALERT_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_SECOND_ALERT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SECOND_ALERT_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReminderMockMvc;

    private Reminder reminder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reminder createEntity(EntityManager em) {
        Reminder reminder = new Reminder()
            .description(DEFAULT_DESCRIPTION)
            .eventDate(DEFAULT_EVENT_DATE)
            .enabled(DEFAULT_ENABLED)
            .alertDate(DEFAULT_ALERT_DATE)
            .secondAlertDate(DEFAULT_SECOND_ALERT_DATE)
            .comment(DEFAULT_COMMENT)
            .emailEnabled(DEFAULT_EMAIL_ENABLED)
            .smsEnabled(DEFAULT_SMS_ENABLED)
            .alertTime(DEFAULT_ALERT_TIME)
            .secondAlertTime(DEFAULT_SECOND_ALERT_TIME);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        reminder.setCustomer(customer);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        reminder.setVehicle(vehicle);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        reminder.setGarage(garage);
        return reminder;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reminder createUpdatedEntity(EntityManager em) {
        Reminder reminder = new Reminder()
            .description(UPDATED_DESCRIPTION)
            .eventDate(UPDATED_EVENT_DATE)
            .enabled(UPDATED_ENABLED)
            .alertDate(UPDATED_ALERT_DATE)
            .secondAlertDate(UPDATED_SECOND_ALERT_DATE)
            .comment(UPDATED_COMMENT)
            .emailEnabled(UPDATED_EMAIL_ENABLED)
            .smsEnabled(UPDATED_SMS_ENABLED)
            .alertTime(UPDATED_ALERT_TIME)
            .secondAlertTime(UPDATED_SECOND_ALERT_TIME);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        reminder.setCustomer(customer);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createUpdatedEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        reminder.setVehicle(vehicle);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createUpdatedEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        reminder.setGarage(garage);
        return reminder;
    }

    @BeforeEach
    public void initTest() {
        reminder = createEntity(em);
    }

    @Test
    @Transactional
    public void createReminder() throws Exception {
        int databaseSizeBeforeCreate = reminderRepository.findAll().size();
        // Create the Reminder
        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isCreated());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeCreate + 1);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReminder.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testReminder.isEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testReminder.getAlertDate()).isEqualTo(DEFAULT_ALERT_DATE);
        assertThat(testReminder.getSecondAlertDate()).isEqualTo(DEFAULT_SECOND_ALERT_DATE);
        assertThat(testReminder.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testReminder.isEmailEnabled()).isEqualTo(DEFAULT_EMAIL_ENABLED);
        assertThat(testReminder.isSmsEnabled()).isEqualTo(DEFAULT_SMS_ENABLED);
        assertThat(testReminder.getAlertTime()).isEqualTo(DEFAULT_ALERT_TIME);
        assertThat(testReminder.getSecondAlertTime()).isEqualTo(DEFAULT_SECOND_ALERT_TIME);
    }

    @Test
    @Transactional
    public void createReminderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reminderRepository.findAll().size();

        // Create the Reminder with an existing ID
        reminder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setDescription(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEventDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setEventDate(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setEnabled(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAlertDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setAlertDate(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setEmailEnabled(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSmsEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = reminderRepository.findAll().size();
        // set the field null
        reminder.setSmsEnabled(null);

        // Create the Reminder, which fails.


        restReminderMockMvc.perform(post("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReminders() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get all the reminderList
        restReminderMockMvc.perform(get("/api/reminders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reminder.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].alertDate").value(hasItem(DEFAULT_ALERT_DATE.toString())))
            .andExpect(jsonPath("$.[*].secondAlertDate").value(hasItem(DEFAULT_SECOND_ALERT_DATE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].emailEnabled").value(hasItem(DEFAULT_EMAIL_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].smsEnabled").value(hasItem(DEFAULT_SMS_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].alertTime").value(hasItem(sameInstant(DEFAULT_ALERT_TIME))))
            .andExpect(jsonPath("$.[*].secondAlertTime").value(hasItem(sameInstant(DEFAULT_SECOND_ALERT_TIME))));
    }
    
    @Test
    @Transactional
    public void getReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        // Get the reminder
        restReminderMockMvc.perform(get("/api/reminders/{id}", reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reminder.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.eventDate").value(DEFAULT_EVENT_DATE.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.alertDate").value(DEFAULT_ALERT_DATE.toString()))
            .andExpect(jsonPath("$.secondAlertDate").value(DEFAULT_SECOND_ALERT_DATE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.emailEnabled").value(DEFAULT_EMAIL_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.smsEnabled").value(DEFAULT_SMS_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.alertTime").value(sameInstant(DEFAULT_ALERT_TIME)))
            .andExpect(jsonPath("$.secondAlertTime").value(sameInstant(DEFAULT_SECOND_ALERT_TIME)));
    }
    @Test
    @Transactional
    public void getNonExistingReminder() throws Exception {
        // Get the reminder
        restReminderMockMvc.perform(get("/api/reminders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();

        // Update the reminder
        Reminder updatedReminder = reminderRepository.findById(reminder.getId()).get();
        // Disconnect from session so that the updates on updatedReminder are not directly saved in db
        em.detach(updatedReminder);
        updatedReminder
            .description(UPDATED_DESCRIPTION)
            .eventDate(UPDATED_EVENT_DATE)
            .enabled(UPDATED_ENABLED)
            .alertDate(UPDATED_ALERT_DATE)
            .secondAlertDate(UPDATED_SECOND_ALERT_DATE)
            .comment(UPDATED_COMMENT)
            .emailEnabled(UPDATED_EMAIL_ENABLED)
            .smsEnabled(UPDATED_SMS_ENABLED)
            .alertTime(UPDATED_ALERT_TIME)
            .secondAlertTime(UPDATED_SECOND_ALERT_TIME);

        restReminderMockMvc.perform(put("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedReminder)))
            .andExpect(status().isOk());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
        Reminder testReminder = reminderList.get(reminderList.size() - 1);
        assertThat(testReminder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReminder.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testReminder.isEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testReminder.getAlertDate()).isEqualTo(UPDATED_ALERT_DATE);
        assertThat(testReminder.getSecondAlertDate()).isEqualTo(UPDATED_SECOND_ALERT_DATE);
        assertThat(testReminder.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testReminder.isEmailEnabled()).isEqualTo(UPDATED_EMAIL_ENABLED);
        assertThat(testReminder.isSmsEnabled()).isEqualTo(UPDATED_SMS_ENABLED);
        assertThat(testReminder.getAlertTime()).isEqualTo(UPDATED_ALERT_TIME);
        assertThat(testReminder.getSecondAlertTime()).isEqualTo(UPDATED_SECOND_ALERT_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingReminder() throws Exception {
        int databaseSizeBeforeUpdate = reminderRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReminderMockMvc.perform(put("/api/reminders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reminder)))
            .andExpect(status().isBadRequest());

        // Validate the Reminder in the database
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReminder() throws Exception {
        // Initialize the database
        reminderRepository.saveAndFlush(reminder);

        int databaseSizeBeforeDelete = reminderRepository.findAll().size();

        // Delete the reminder
        restReminderMockMvc.perform(delete("/api/reminders/{id}", reminder.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
