package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.config.ApplicationProperties;
import gms4u.domain.Booking;
import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.Vehicle;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.BookingRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static gms4u.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BookingResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class BookingResourceIT {

    private static final LocalDate DEFAULT_BOOKING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BOOKING_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_BOOKING_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BOOKING_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_FURTHER_INSTRUCTION = "AAAAAAAAAA";
    private static final String UPDATED_FURTHER_INSTRUCTION = "BBBBBBBBBB";

    private static final BookingStatus DEFAULT_STATUS = BookingStatus.IN_PROGRESS;
    private static final BookingStatus UPDATED_STATUS = BookingStatus.COMPLETED;

    @Autowired
    private BookingRepository bookingRepository;

    @Mock
    private BookingRepository bookingRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingMockMvc;

    @Autowired
    private ApplicationProperties applicationProperties;

    private Booking booking;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createEntity(EntityManager em) {
        Booking booking = new Booking()
            .bookingDate(DEFAULT_BOOKING_DATE)
            .bookingTime(DEFAULT_BOOKING_TIME)
            .furtherInstruction(DEFAULT_FURTHER_INSTRUCTION)
            .status(DEFAULT_STATUS);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        booking.setGarage(garage);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        booking.setVehicle(vehicle);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        booking.setCustomer(customer);
        return booking;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createUpdatedEntity(EntityManager em) {
        Booking booking = new Booking()
            .bookingDate(UPDATED_BOOKING_DATE)
            .bookingTime(UPDATED_BOOKING_TIME)
            .furtherInstruction(UPDATED_FURTHER_INSTRUCTION)
            .status(UPDATED_STATUS);
        // Add required entity
        Garage garage;
        if (TestUtil.findAll(em, Garage.class).isEmpty()) {
            garage = GarageResourceIT.createUpdatedEntity(em);
            em.persist(garage);
            em.flush();
        } else {
            garage = TestUtil.findAll(em, Garage.class).get(0);
        }
        booking.setGarage(garage);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createUpdatedEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        booking.setVehicle(vehicle);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        booking.setCustomer(customer);
        return booking;
    }

    @BeforeEach
    public void initTest() {
        booking = createEntity(em);
    }

    @Test
    @Transactional
    public void createBooking() throws Exception {
        int databaseSizeBeforeCreate = bookingRepository.findAll().size();

        // Create the Booking
        restBookingMockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isCreated());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate + 1);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);

        LocalTime bookingTime = testBooking.getBookingTime().toLocalTime();
        LocalDate bookingDate = testBooking.getBookingDate();
        assertThat(testBooking.getBookingTime()).isEqualTo(ZonedDateTime.of(bookingDate, bookingTime, ZoneId.systemDefault()));
        assertThat(testBooking.getFurtherInstruction()).isEqualTo(DEFAULT_FURTHER_INSTRUCTION);
        assertThat(testBooking.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createBookingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookingRepository.findAll().size();

        // Create the Booking with an existing ID
        booking.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingMockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingRepository.findAll().size();
        // set the field null
        booking.setStatus(null);

        // Create the Booking, which fails.

        restBookingMockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isCreated());

        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeTest + 1);
    }

    @Test
    @Transactional
    public void getAllBookings() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList
        restBookingMockMvc.perform(get("/api/bookings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].bookingTime").value(hasItem(sameInstant(DEFAULT_BOOKING_TIME))))
            .andExpect(jsonPath("$.[*].furtherInstruction").value(hasItem(DEFAULT_FURTHER_INSTRUCTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBookingsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingMockMvc.perform(get("/api/bookings?eagerload=true"))
            .andExpect(status().isOk());

        verify(bookingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBookingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingMockMvc.perform(get("/api/bookings?eagerload=true"))
            .andExpect(status().isOk());

        verify(bookingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get the booking
        restBookingMockMvc.perform(get("/api/bookings/{id}", booking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(booking.getId().intValue()))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.bookingTime").value(sameInstant(DEFAULT_BOOKING_TIME)))
            .andExpect(jsonPath("$.furtherInstruction").value(DEFAULT_FURTHER_INSTRUCTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBooking() throws Exception {
        // Get the booking
        restBookingMockMvc.perform(get("/api/bookings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking
        Booking updatedBooking = bookingRepository.findById(booking.getId()).get();
        // Disconnect from session so that the updates on updatedBooking are not directly saved in db
        em.detach(updatedBooking);
        updatedBooking
            .bookingDate(UPDATED_BOOKING_DATE)
            .bookingTime(UPDATED_BOOKING_TIME)
            .furtherInstruction(UPDATED_FURTHER_INSTRUCTION)
            .status(UPDATED_STATUS);

        restBookingMockMvc.perform(put("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBooking)))
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        LocalTime bookingTime = testBooking.getBookingTime().toLocalTime();
        LocalDate bookingDate = testBooking.getBookingDate();
        assertThat(testBooking.getBookingTime()).isEqualTo(ZonedDateTime.of(bookingDate, bookingTime, ZoneId.systemDefault()));
        assertThat(testBooking.getFurtherInstruction()).isEqualTo(UPDATED_FURTHER_INSTRUCTION);
        assertThat(testBooking.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingMockMvc.perform(put("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeDelete = bookingRepository.findAll().size();

        // Delete the booking
        restBookingMockMvc.perform(delete("/api/bookings/{id}", booking.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
