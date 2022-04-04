package gms4u.web.rest;

import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.Vehicle;
import gms4u.repository.CustomerRepository;
import gms4u.repository.GarageRepository;
import gms4u.repository.VehicleRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.MailService;
import gms4u.service.ReminderService;
import gms4u.service.dto.FileDto;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.eclipse.birt.report.engine.api.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing {@link gms4u.domain.Customer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CustomerResource {

    private static final String ENTITY_NAME = "customer";
    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);
    private final CustomerRepository customerRepository;
    private final ReminderService reminderService;
    private final GarageAdminService garageAdminService;
    private final GarageRepository garageRepository;
    private final VehicleRepository vehicleRepository;
    private final MailService mailService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public CustomerResource(CustomerRepository customerRepository, ReminderService reminderService, GarageAdminService garageAdminService, GarageRepository garageRepository, VehicleRepository vehicleRepository, MailService mailService) {
        this.customerRepository = customerRepository;
        this.reminderService = reminderService;
        this.garageAdminService = garageAdminService;
        this.garageRepository = garageRepository;
        this.vehicleRepository = vehicleRepository;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /customers} : Create a new customer.
     *
     * @param customer the customer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customer, or with status {@code 400 (Bad Request)} if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer : {}", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }


        Customer result;

        Optional<Customer> customerOptional = customerRepository.findByEmailIgnoreCaseAndPhoneNumberIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(customer.getEmail().toLowerCase(Locale.ROOT), customer.getPhoneNumber().toLowerCase(Locale.ROOT), customer.getFirstName().toLowerCase(Locale.ROOT), customer.getLastName().toLowerCase(Locale.ROOT));

        if (customerOptional.isPresent()) {
            Customer c = customerOptional.get();
            customer.getGarages().stream().filter(cg -> !c.getGarages().contains(cg)).findAny().ifPresent(g -> {
                c.addGarages(g);
                reminderService.sendCustomerMessage(c, g);
                mailService.sendCustomerRegistrationEmail(c, g);
            });
            result = customerRepository.save(c);
        } else {
            result = customerRepository.save(customer);
        }

        Customer finalResult = result;
        Set<Garage> garages = finalResult.getGarages();
        if (!garages.isEmpty()) {
            if (garages.size() > 1) {
                customer.getGarages().stream().filter(cg -> !finalResult.getGarages().contains(cg)).findAny().ifPresent(g ->
                    mailService.sendCustomerRegistrationEmail(finalResult, g)
                );
            } else {
                customer.getGarages().stream().findAny().ifPresent(g ->
                    mailService.sendCustomerRegistrationEmail(finalResult, g)
                );
            }
        }


        return ResponseEntity.created(new URI("/api/customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customers} : Updates an existing customer.
     *
     * @param customer the customer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customer,
     * or with status {@code 400 (Bad Request)} if the customer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customers")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to update Customer : {}", customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<Customer> customerOptional = customerRepository.findByEmailIgnoreCaseAndPhoneNumberIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(customer.getEmail().toLowerCase(Locale.ROOT), customer.getPhoneNumber().toLowerCase(Locale.ROOT), customer.getFirstName().toLowerCase(Locale.ROOT), customer.getLastName().toLowerCase(Locale.ROOT));

        if (customerOptional.isPresent()) {
            throw new BadRequestAlertException("Customer details already exists", ENTITY_NAME, "idexists");
        }

        Customer result = customerRepository.save(customer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customer.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /customers} : get all the customers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        log.debug("REST request to get all Customers");
        return customerRepository.findAll();
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer's vehicles.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customers/{id}/vehicles")
    public Set<Vehicle> getCustomerVehicles(@PathVariable Long id) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);

        if (customer.isPresent()) {

            return customerRepository.getCustomerVehicles(customer.get().getId());
        }
        return new HashSet<>();
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer's vehicles.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/garages/{garageId}/customers/{id}/vehicles")
    public Set<Vehicle> getCustomerVehicles(@PathVariable(value = "id") Long id, @PathVariable(value = "garageId") Long garageId) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);

        if (customer.isPresent()) {
            Set<Vehicle> customerVehicles = customerRepository.getCustomerVehicles(customer.get().getId(), garageId);
            if (!customerVehicles.isEmpty())
                return customerVehicles;
        }
        return new HashSet<>();
    }

    @GetMapping(value = "/garages/{garageId}/customers/{customerId}/vehicles/{vehicleId}/service-history/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadServiceHistoryReport(@PathVariable Long customerId, @PathVariable Long vehicleId, @PathVariable Long garageId) throws EngineException, IOException {
        log.debug("REST request to download service history.");

        FileDto fileDto = garageAdminService.createServiceHistoryReport(customerId, vehicleId, garageId);
        if (fileDto.getData() != null && fileDto.getData().length > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("service-history-report-file", fileDto.getFilename());
            return new ResponseEntity<>(fileDto.getData(), headers, HttpStatus.OK);
        }

        return ResponseEntity.ok(new byte[]{});
    }

    @PostMapping(value = "/garages/{garageId}/customers/{customerId}/vehicles/{vehicleId}/service-history")
    public ResponseEntity<Void> sendServiceHistoryReportPDF(@PathVariable Long customerId, @PathVariable Long vehicleId, @PathVariable Long garageId) throws IOException, EngineException {
        log.debug("REST request to send Service History Report PDF: {}", customerId);
        FileDto fileDto = garageAdminService.createServiceHistoryReport(customerId, vehicleId, garageId);
        if (fileDto.getData() != null && fileDto.getData().length > 0) {

            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found with id" + customerId));
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id " + vehicleId));
            Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new IllegalArgumentException("Could not find garage with id " + garageId));
            mailService.sendServiceHistoryReportMail(customer, vehicle, garage, fileDto);
            return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Service History Report sent.", ENTITY_NAME)).build();
        }

        return ResponseEntity.unprocessableEntity().build();

    }

    /**
     * {@code DELETE  /customers/:id} : delete the "id" customer.
     *
     * @param id the id of the customer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("REST request to delete Customer : {}", id);
        customerRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
