package gms4u.web.rest;

import gms4u.domain.Booking;
import gms4u.domain.Invoice;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.BookingRepository;
import gms4u.repository.InvoiceRepository;
import gms4u.service.GarageAdminService;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link gms4u.domain.Invoice}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class InvoiceResource {

    private static final String ENTITY_NAME = "invoice";
    private final Logger log = LoggerFactory.getLogger(InvoiceResource.class);
    private final InvoiceRepository invoiceRepository;
    private final GarageAdminService garageAdminService;
    private final BookingRepository bookingRepository;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public InvoiceResource(InvoiceRepository invoiceRepository, GarageAdminService garageAdminService, BookingRepository bookingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.garageAdminService = garageAdminService;
        this.bookingRepository = bookingRepository;
    }

    /**
     * {@code POST  /invoices} : Create a new invoice.
     *
     * @param invoice the invoice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoice, or with status {@code 400 (Bad Request)} if the invoice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody Invoice invoice) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to save Invoice : {}", invoice);
        if (invoice.getId() != null) {
            throw new BadRequestAlertException("A new invoice cannot already have an ID", ENTITY_NAME, "idexists");
        }


        Booking booking = invoice.getBooking();
        Invoice result = new Invoice();
        if (booking != null) {
            List<BookingStatus> bookingStatuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.CANCELLED, BookingStatus.IN_PROGRESS, BookingStatus.INVOICED);
            if (booking.getId() != null) {
                Optional<Booking> byId = bookingRepository.findById(booking.getId());
                if (booking.getStatus().equals(BookingStatus.PENDING) && byId.isPresent())
                    booking = byId.get();

                if (booking.getStatus() != null && bookingStatuses.contains(booking.getStatus()))
                    throw new BadRequestAlertException("A new booking cannot already have an ID", "booking", "idexists");
            }

            garageAdminService.saveCustomJobs(booking);

            garageAdminService.getOrSaveCustomer(booking);

            garageAdminService.
                getOrSaveVehicle(booking);

            garageAdminService.handleBookingTime(booking);
            garageAdminService.getOrSaveQuote(booking);

            invoice.setInvoiceDate(ZonedDateTime.now());
            booking.setStatus(BookingStatus.INVOICED);
            if (invoice.getReference() == null)
                invoice.setReference(garageAdminService.generateInvoiceReference(booking));
            Booking save = bookingRepository.save(booking);
            invoice.setBooking(save);
            save.setInvoice(invoice);

            result = invoiceRepository.save(invoice);


        }


        return ResponseEntity.created(new URI("/api/invoices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, String.valueOf(result.getId())))
            .body(result);
    }


    /**
     * {@code PUT  /invoices} : Updates an existing invoice.
     *
     * @param invoice the invoice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoice,
     * or with status {@code 400 (Bad Request)} if the invoice is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/invoices")
    public ResponseEntity<Invoice> updateInvoice(@Valid @RequestBody Invoice invoice) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to update Invoice : {}", invoice);
        if (invoice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }


        Booking booking = invoice.getBooking();
        if (booking != null) {


            garageAdminService.saveCustomJobs(booking);

            garageAdminService.getOrSaveCustomer(booking);

            garageAdminService.
                getOrSaveVehicle(booking);

            if (invoice.getReference() == null)
                invoice.setReference(garageAdminService.generateInvoiceReference(booking));

            garageAdminService.handleBookingTime(booking);

            garageAdminService.getOrSaveQuote(booking);

            booking.setStatus(BookingStatus.INVOICED);
            booking.setInvoice(invoice);
            invoice.setBooking(bookingRepository.save(booking));
        }

        Invoice result = invoiceRepository.save(invoice);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, invoice.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /invoices} : get all the invoices.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoices in body.
     */
    @GetMapping("/invoices")
    public List<Invoice> getAllInvoices() {
        log.debug("REST request to get all Invoices");
        return invoiceRepository.findAll();
    }

    /**
     * {@code GET  /invoices/:id} : get the "id" invoice.
     *
     * @param id the id of the invoice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        log.debug("REST request to get Invoice : {}", id);
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(invoice);
    }

    /**
     * {@code DELETE  /invoices/:id} : delete the "id" invoice.
     *
     * @param id the id of the invoice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        log.debug("REST request to delete Invoice : {}", id);
        garageAdminService.deleteInvoice(id);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
