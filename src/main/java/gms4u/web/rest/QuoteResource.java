package gms4u.web.rest;

import gms4u.domain.Booking;
import gms4u.domain.Quote;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.BookingRepository;
import gms4u.repository.CustomerRepository;
import gms4u.repository.QuoteRepository;
import gms4u.repository.VehicleRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.MailService;
import gms4u.service.SMSService;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link gms4u.domain.Quote}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class QuoteResource {

    private static final String ENTITY_NAME = "quote";
    private final Logger log = LoggerFactory.getLogger(QuoteResource.class);
    private final QuoteRepository quoteRepository;
    private final GarageAdminService garageAdminService;
    private final BookingRepository bookingRepository;
    private final MailService mailService;
    private final SMSService smsService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public QuoteResource(QuoteRepository quoteRepository, VehicleRepository vehicleRepository, CustomerRepository customerRepository, GarageAdminService garageAdminService, BookingRepository bookingRepository, MailService mailService, SMSService smsService) {
        this.quoteRepository = quoteRepository;
        this.garageAdminService = garageAdminService;
        this.bookingRepository = bookingRepository;
        this.mailService = mailService;
        this.smsService = smsService;
    }

    /**
     * {@code POST  /quotes} : Create a new quote.
     *
     * @param quote the quote to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quote, or with status {@code 400 (Bad Request)} if the quote has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/quotes")
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to save Quote : {}", quote);
        if (quote.getId() != null) {
            throw new BadRequestAlertException("A new quote cannot already have an ID", ENTITY_NAME, "idexists");
        }
        quote.setQuoteDate(LocalDate.now());

        Booking booking = quote.getBooking();
        if (booking != null) {
            if (booking.getId() != null) {
                throw new BadRequestAlertException("A new booking cannot already have an ID", "booking", "idexists");
            }

            garageAdminService.saveCustomJobs(booking);

            garageAdminService.getOrSaveCustomer(booking);

            garageAdminService.getOrSaveVehicle(booking);

            booking.setStatus(BookingStatus.PENDING);
            booking.setQuote(quote);
            if (quote.getReference() == null)
                quote.setReference(garageAdminService.generateQuoteReference(booking));


            quote.setBooking(bookingRepository.save(booking));
        }

        Quote result = quoteRepository.save(quote);

//        mailService.sendQuotationMail(result);

        return ResponseEntity.created(new URI("/api/quotes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quotes} : Updates an existing quote.
     *
     * @param quote the quote to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quote,
     * or with status {@code 400 (Bad Request)} if the quote is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quote couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/quotes")
    public ResponseEntity<Quote> updateQuote(@RequestBody Quote quote) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to update Quote : {}", quote);
        if (quote.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Booking booking = quote.getBooking();
        if (booking != null) {

            garageAdminService.saveCustomJobs(booking);

            garageAdminService.getOrSaveCustomer(booking);

            garageAdminService.getOrSaveVehicle(booking);
            booking.setQuote(quote);
            if (quote.getReference() == null)
                quote.setReference(garageAdminService.generateQuoteReference(booking));


            quote.setBooking(bookingRepository.save(booking));
        }

        Quote result = quoteRepository.save(quote);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, quote.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /quotes} : get all the quotes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quotes in body.
     */
    @GetMapping("/quotes")
    public List<Quote> getAllQuotes() {
        log.debug("REST request to get all Quotes");
        return quoteRepository.findAll();
    }

    /**
     * {@code GET  /quotes/:id} : get the "id" quote.
     *
     * @param id the id of the quote to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quote, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/quotes/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable Long id) {
        log.debug("REST request to get Quote : {}", id);
        Optional<Quote> quote = quoteRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(quote);
    }

    /**
     * {@code DELETE  /quotes/:id} : delete the "id" quote.
     *
     * @param id the id of the quote to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/quotes/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        log.debug("REST request to delete Quote : {}", id);
        garageAdminService.deleteQuote(id);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
