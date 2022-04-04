package gms4u.web.rest;

import gms4u.domain.Booking;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.BookingRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.MailService;
import gms4u.service.ReminderService;
import gms4u.service.dto.FileDto;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing {@link gms4u.domain.Booking}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BookingResource {

    private static final String ENTITY_NAME = "booking";
    private final Logger log = LoggerFactory.getLogger(BookingResource.class);
    private final BookingRepository bookingRepository;
    private final GarageAdminService garageAdminService;
    private final MailService mailService;
    private final ReminderService reminderService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public BookingResource(BookingRepository bookingRepository, GarageAdminService garageAdminService, MailService mailService, ReminderService reminderService) {
        this.bookingRepository = bookingRepository;
        this.garageAdminService = garageAdminService;
        this.mailService = mailService;
        this.reminderService = reminderService;
    }

    /**
     * {@code POST  /bookings} : Create a new booking.
     *
     * @param booking the booking to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new booking, or with status {@code 400 (Bad Request)} if the booking has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) throws URISyntaxException, SchedulerException, MalformedURLException {
        log.debug("REST request to save Booking : {}", booking);
        if (booking.getId() != null) {
            throw new BadRequestAlertException("A new booking cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (booking.getStatus() == null)
            booking.setStatus(BookingStatus.CONFIRMED);

        garageAdminService.saveCustomJobs(booking);

        garageAdminService.getOrSaveCustomer(booking);

        try {
            garageAdminService.getOrSaveVehicle(booking);

        } catch (
            HttpClientErrorException error) {
            if (error.getStatusCode().is4xxClientError() && error.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new BadRequestAlertException("Vehicle not found.", ENTITY_NAME, "vehicle-invalid");
        }

        if (booking.getReference() == null)
            booking.setReference(garageAdminService.generateBookingReference(booking));

        garageAdminService.handleBookingTime(booking);

        garageAdminService.getOrSaveQuote(booking);

        Booking result = bookingRepository.save(booking);

        mailService.sendBookingConfirmationMail(result);

        reminderService.sendBookingConfirmationMessage(booking);

        reminderService.scheduleBookingReminder(booking);


        return ResponseEntity.created(new URI("/api/bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code PUT  /bookings} : Updates an existing booking.
     *
     * @param booking the booking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated booking,
     * or with status {@code 400 (Bad Request)} if the booking is not valid,
     * or with status {@code 500 (Internal Server Error)} if the booking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bookings")
    public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to update Booking : {}", booking);
        if (booking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        garageAdminService.saveCustomJobs(booking);

        garageAdminService.getOrSaveCustomer(booking);

        try {
            garageAdminService.getOrSaveVehicle(booking);

        } catch (
            HttpClientErrorException error) {
            if (error.getStatusCode().is4xxClientError() && error.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new BadRequestAlertException("Vehicle not found.", ENTITY_NAME, "vehicle-invalid");
        }

        if (booking.getReference() == null)
            booking.setReference(garageAdminService.generateBookingReference(booking));

        garageAdminService.handleBookingTime(booking);

        garageAdminService.getOrSaveQuote(booking);

        Booking result = bookingRepository.save(booking);


        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, booking.getId().toString()))
            .body(result);
    }



    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the booking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the booking, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(value = "/bookings/{id}/invoices", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadBookingInvoicePDF(@PathVariable Long id) throws IOException {
        log.debug("REST request to get Booking : {}", id);
        Optional<Booking> booking = bookingRepository.findOneWithEagerRelationships(id);
        if (booking.isPresent()) {
            FileDto fileDto = garageAdminService.createInvoice(booking.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("invoice-file", fileDto.getFilename());
            return new ResponseEntity<>(fileDto.getData(), headers, HttpStatus.OK);
        }

        return ResponseEntity.ok(new byte[]{});
    }

    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the booking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the booking, or with status {@code 404 (Not Found)}.
     */
    @PostMapping(value = "/bookings/{id}/invoices")
    public ResponseEntity<Void> sendBookingInvoicePDF(@PathVariable Long id) throws IOException {
        log.debug("REST request to send Booking Invoice PDF: {}", id);
        Optional<Booking> booking = bookingRepository.findOneWithEagerRelationships(id);
        if (booking.isPresent()) {
            FileDto fileDto = garageAdminService.createInvoice(booking.get());
            if (booking.get().getInvoice() != null && booking.get().getStatus().equals(BookingStatus.INVOICED)) {
                mailService.sendInvoiceMail(booking.get().getInvoice(), fileDto);
                return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Invoice sent.", ENTITY_NAME)).build();
            } else {
                mailService.sendQuotationMail(booking.get().getQuote(), fileDto);
                return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Quote sent.", ENTITY_NAME)).build();
            }
        }

        return ResponseEntity.unprocessableEntity().build();

    }

    /**
     * {@code GET  /bookings} : get all the bookings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter    the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("/bookings")
    public List<Booking> getAllBookings(@RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        if ("quote-is-null".equals(filter)) {
            log.debug("REST request to get all Bookings where quote is null");
            return StreamSupport
                .stream(bookingRepository.findAll().spliterator(), false)
                .filter(booking -> booking.getQuote() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all Bookings");
        return bookingRepository.findAllWithEagerRelationships();
    }


    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the booking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the booking, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
        Optional<Booking> booking = bookingRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(booking);
    }

    /**
     * {@code DELETE  /bookings/:id} : delete the "id" booking.
     *
     * @param id the id of the booking to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        log.debug("REST request to delete Booking : {}", id);

        garageAdminService.deleteBooking(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }


    /**
     * {@code GET  /bookings} : get all the service history.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter    the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("/garages/{garageId}/customers/{id}/vehicles/{vehicleId}/history")
    public List<Booking> getServiceHistories(@PathVariable Long id, @PathVariable Long vehicleId, @PathVariable Long garageId, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {

        log.debug("REST request to get service history");
        return garageAdminService.getFilteredServiceHistory(garageId, id, vehicleId);
    }
}
