package gms4u.web.rest;

import gms4u.domain.*;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.GarageRepository;
import gms4u.service.FileStoreService;
import gms4u.service.GarageAdminService;
import gms4u.service.dto.FileDto;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

/**
 * REST controller for managing {@link gms4u.domain.Garage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GarageResource {

    private static final String ENTITY_NAME = "garage";
    private final Logger log = LoggerFactory.getLogger(GarageResource.class);
    private final GarageRepository garageRepository;
    private final GarageAdminService garageAdminService;
    private final FileStoreService fileStoreService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public GarageResource(GarageRepository garageRepository, GarageAdminService garageAdminService, FileStoreService fileStoreService) {
        this.garageRepository = garageRepository;
        this.garageAdminService = garageAdminService;
        this.fileStoreService = fileStoreService;
    }

    /**
     * {@code POST  /garages} : Create a new garage.
     *
     * @param garage the garage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new garage, or with status {@code 400 (Bad Request)} if the garage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/garages")
    public ResponseEntity<Garage> createGarage(@RequestBody Garage garage) throws URISyntaxException {
        log.debug("REST request to save Garage : {}", garage);
        if (garage.getId() != null) {
            throw new BadRequestAlertException("A new garage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Garage result = garageRepository.save(garage);
        return ResponseEntity.created(new URI("/api/garages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /garages} : Updates an existing garage.
     *
     * @param garage the garage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated garage,
     * or with status {@code 400 (Bad Request)} if the garage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the garage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/garages")
    public ResponseEntity<Garage> updateGarage(@RequestBody Garage garage) throws URISyntaxException {
        log.debug("REST request to update Garage : {}", garage);
        if (garage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Garage result = garageRepository.save(garage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, garage.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /garages} : get all the garages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of garages in body.
     */
    @GetMapping("/garages")
    public List<Garage> getAllGarages() {
        log.debug("REST request to get all Garages");
        return garageRepository.findAll();
    }

    /**
     * {@code GET  /garages/:id} : get the "id" garage.
     *
     * @param id the id of the garage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the garage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/garages/{id}")
    public ResponseEntity<Garage> getGarage(@PathVariable Long id) {
        log.debug("REST request to get Garage : {}", id);
        Optional<Garage> garage = garageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(garage);
    }

    /**
     * {@code DELETE  /garages/:id} : delete the "id" garage.
     *
     * @param id the id of the garage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/garages/{id}")
    public ResponseEntity<Void> deleteGarage(@PathVariable Long id) {
        log.debug("REST request to delete Garage : {}", id);
        garageRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    @DeleteMapping("/garages/{id}/vehicles/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id, @PathVariable Long vehicleId) {
        log.debug("REST request to delete Vehicle : {}", vehicleId);

        garageAdminService.deleteVehicleFromGarage(vehicleId, id);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, "vehicle", vehicleId.toString())).build();
    }

    @DeleteMapping("/garages/{id}/customers/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id, @PathVariable Long customerId) {
        log.debug("REST request to delete Customer : {}", customerId);

        garageAdminService.deleteCustomerFromGarage(customerId, id);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, "customer", customerId.toString())).build();
    }

    /**
     * {@code GET  /garages/:id/bookings} : get garage all the bookings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param statuses  the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("/garages/{id}/bookings")
    public List<Booking> getGarageAllBookings(@PathVariable Long id, @RequestParam(required = false) List<BookingStatus> statuses, @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date, @RequestParam(required = false, defaultValue = "false") boolean eagerload, Pageable pageable) {
        log.debug("REST request to get all Bookings from garage id " + id);

        if (date != null && statuses != null && !statuses.isEmpty()) {
            return garageAdminService.getGarageTodayBookingsWithStatuses(id, statuses, date, pageable);
        } else if (date == null && statuses != null && !statuses.isEmpty()) {
            return garageAdminService.getGarageAllBookingsWithStatuses(id, statuses, pageable);
        }

        return garageAdminService.getGarageAllBookings(id);
    }

    /**
     * {@code GET  /garages/:id/bookings} : get garage all the bookings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("/garages/{id}/service-histories")
    public List<Booking> getGarageAllServiceHistory(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean eagerload, Pageable pageable) {
        log.debug("REST request to get all Bookings from garage id " + id);
        return garageAdminService.getGarageAllServiceHistory(id, pageable);
    }


    /**
     * {@code GET  /garages/:id/bookings/{bookingId}/jobs} : get garage all the bookings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("/garages/{id}/bookings/{bookingId}/jobs")
    public List<Job> getGarageBookingJobs(@PathVariable Long bookingId, @PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Bookings from garage id " + id);
        return garageAdminService.getJobsByBookingId(bookingId, id);
    }

    @GetMapping("/garages/{id}/quotes")
    public List<Quote> getGarageAllQuotations(@PathVariable Long id, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") boolean eagerload, Pageable pageable) {
        log.debug("REST request to get all Quotations from garage id " + id);
        Optional<Sort.Order> quoteDateOptional = pageable.getSort().get().filter(p -> p.getProperty().equals("quoteDate")).findAny();
        if (quoteDateOptional.isPresent()) {
            Sort.Order quoteDate = quoteDateOptional.get();
            String property = "quote." + quoteDate.getProperty();
            Sort.Direction direction = quoteDate.getDirection();
            return garageAdminService.getGarageAllQuotations(id, Sort.by(new Sort.Order(direction, property)));
        }

        return garageAdminService.getGarageAllQuotations(id);

    }


    @GetMapping("/garages/{id}/jobs")
    public List<Job> getGarageAllJobs(@PathVariable Long id, @RequestParam(required = false) Long bookingId, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Jobs from garage id " + id);

        Set<Job> garageAllJobs = new HashSet<>(garageAdminService.getGarageAllJobs(id));

        if (bookingId != null) {
            List<Job> jobsByBookingId = garageAdminService.getJobsByBookingId(bookingId, id);
            garageAllJobs.addAll(jobsByBookingId);
        }

        return new ArrayList<>(garageAllJobs);

    }


    @GetMapping("/garages/{id}/customers")
    public List<Customer> getGarageAllCustomers(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Customers from garage id " + id);
        return garageAdminService.getGarageAllCustomers(id);
    }

    @GetMapping("/garages/{id}/vehicles")
    public List<Vehicle> getGarageAllVehicles(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Vehicle from garage id " + id);
        return garageAdminService.getGarageAllVehicles(id);
    }

    @GetMapping("/garages/{id}/invoices")
    public List<Invoice> getGarageAllInvoices(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean eagerload, Pageable pageable) {
        log.debug("REST request to get all Invoice from garage id " + id);

        Optional<Sort.Order> issueDateOptional = pageable.getSort().get().filter(p -> p.getProperty().equals("issueDate")).findAny();
        if (issueDateOptional.isPresent()) {
            Sort.Order issueDateOrder = issueDateOptional.get();
            String property = "invoice." + issueDateOrder.getProperty();
            Sort.Direction direction = issueDateOrder.getDirection();
            return garageAdminService.getGarageAllInvoices(id, Sort.by(new Sort.Order(direction, property)));
        }

        return garageAdminService.getGarageAllInvoices(id, pageable.getSort());

    }

    @GetMapping("/garages/{id}/reminders")
    public List<Reminder> getGarageAllReminders(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Invoice from garage id " + id);
        return garageAdminService.getGarageAllReminders(id, date);
    }

    /**
     * {@code POST  /garages} : Create a new garage.
     *
     * @param id to get garage details to upload file..
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the new garage.
     */
    @PostMapping(value = "/garages/{id}/upload-logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Garage> uploadGarageLogo(@PathVariable Long id, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        log.debug("REST request to upload Garage logo : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Garage result = null;
        Optional<Garage> byId = garageRepository.findById(id);
        if (byId.isPresent()) {
            result = byId.get();
            if (file != null)
                fileStoreService.storeGarageLogo(result, file);
            else {
                if (fileStoreService.deleteFile(result)) {
                    result.setLogoUrl(null);
                    garageRepository.save(result);
                }

            }
        }

        return ResponseEntity.ok()
            .body(result);
    }

    @GetMapping("/garages/{id}/retrieve-logo")
    public ResponseEntity<FileDto> retrieveGarageLogo(@PathVariable Long id) throws IOException {
        log.debug("REST request to upload Garage logo : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        FileDto file = null;
        Optional<Garage> byId = garageRepository.findById(id);
        if (byId.isPresent()) {
            Garage garage = byId.get();
            file = fileStoreService.loadFileAsResource(garage);
            garage.setFile(file);
        }

        return ResponseEntity.ok().body(file);
    }

}
