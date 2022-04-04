package gms4u.web.rest;

import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.Vehicle;
import gms4u.repository.CustomerRepository;
import gms4u.repository.ReminderRepository;
import gms4u.repository.VehicleRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.ReminderService;
import gms4u.service.dto.DvlaVehicle;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing {@link gms4u.domain.Vehicle}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VehicleResource {

    private static final String ENTITY_NAME = "vehicle";
    private final Logger log = LoggerFactory.getLogger(VehicleResource.class);
    private final VehicleRepository vehicleRepository;
    private final GarageAdminService garageAdminService;
    private final CustomerRepository customerRepository;
    private final ReminderService reminderService;
    private final ReminderRepository reminderRepository;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public VehicleResource(VehicleRepository vehicleRepository, GarageAdminService garageAdminService, CustomerRepository customerRepository, ReminderService reminderService, ReminderRepository reminderRepository) {
        this.vehicleRepository = vehicleRepository;
        this.garageAdminService = garageAdminService;
        this.customerRepository = customerRepository;
        this.reminderService = reminderService;
        this.reminderRepository = reminderRepository;
    }

    /**
     * {@code POST  /vehicles} : Create a new vehicle.
     *
     * @param vehicle the vehicle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicle, or with status {@code 400 (Bad Request)} if the vehicle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to save Vehicle : {}", vehicle);
        if (vehicle.getId() != null) {
            throw new BadRequestAlertException("A new vehicle cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Vehicle result = null;

        String registration = vehicle.getRegistration().toUpperCase();



        String make = vehicle.getMake();
        String model = vehicle.getModel();
        String colour = vehicle.getColour();
        LocalDate motExpiryDate = vehicle.getMotExpiryDate();
        

        DvlaVehicle dvlabody = new DvlaVehicle();

        dvlabody.setRegistrationNumber(registration);
        dvlabody.setMake(make);
        dvlabody.setColour(colour);
        dvlabody.setMotExpiryDate(motExpiryDate);
        


        try {
            DvlaVehicle body = garageAdminService.getDvlaVehicle(registration);

            Optional<Vehicle> byRegistration = vehicleRepository.findByRegistration(registration);

            Set<Garage> garages = vehicle.getGarages();
            if (byRegistration.isPresent()) {
                Vehicle v = byRegistration.get();
                Optional<Garage> optionalGarage = garages.stream().filter(vg -> !v.getGarages().contains(vg)).findAny();
                optionalGarage.ifPresent(v::addGarages);
                if (body != null) {
                    v.setMotExpiryDate(body.getMotExpiryDate());
                    v.setRegistration(body.getRegistrationNumber());
                }

                Optional<Customer> anyCustomer = vehicle.getOwners().stream().findAny();
                anyCustomer.map(customer -> customerRepository.findById(customer.getId()).orElse(customer)).ifPresent(c -> {
                    c.getVehicles().add(v);
                    v.getOwners().add(c);
                });

                result = vehicleRepository.save(v);
                Optional<Garage> currentGarage = vehicle.getGarages().stream().findAny();
                if (anyCustomer.isPresent() && currentGarage.isPresent())
                    reminderService.updateMOTReminder(result, anyCustomer.get(), currentGarage.get());


            } else {
                if (body != null) {
                    vehicle.setMotExpiryDate(body.getMotExpiryDate());
                    vehicle.setRegistration(body.getRegistrationNumber());
                }
                Garage garage = garages.stream().findAny().orElseThrow(() -> new IllegalArgumentException("No garage associated."));
                Optional<Customer> anyCustomer = vehicle.getOwners().stream().findAny();
                anyCustomer.map(customer -> customerRepository.findById(customer.getId()).orElse(customer)).ifPresent(c -> {
                    c.getVehicles().add(vehicle);
                    vehicle.getOwners().add(c);
                });
                result = vehicleRepository.save(vehicle);
                if (anyCustomer.isPresent())
                    reminderService.updateMOTReminder(result, anyCustomer.get(), garage);
            }


        } catch (HttpClientErrorException error) {
            // if (error.getStatusCode().is4xxClientError() && error.getStatusCode().equals(HttpStatus.NOT_FOUND))
            
            //     throw new BadRequestAlertException("Vehicle not found.", ENTITY_NAME, "vehicle-invalid");
            Optional<Vehicle> byRegistration = vehicleRepository.findByRegistration(registration);
            Set<Garage> garages = vehicle.getGarages();
            if (byRegistration.isPresent()) {
                Vehicle v = byRegistration.get();
                Optional<Garage> optionalGarage = garages.stream().filter(vg -> !v.getGarages().contains(vg)).findAny();
                optionalGarage.ifPresent(v::addGarages);
                if (dvlabody != null) {
                    v.setMotExpiryDate(dvlabody.getMotExpiryDate());
                    v.setRegistration(dvlabody.getRegistrationNumber());
                }
                Optional<Customer> anyCustomer = vehicle.getOwners().stream().findAny();
                anyCustomer.map(customer -> customerRepository.findById(customer.getId()).orElse(customer)).ifPresent(c -> {
                    c.getVehicles().add(v);
                    v.getOwners().add(c);
                });

                result = vehicleRepository.save(v);
                Optional<Garage> currentGarage = vehicle.getGarages().stream().findAny();
                if (anyCustomer.isPresent() && currentGarage.isPresent())
                    reminderService.updateMOTReminder(result, anyCustomer.get(), currentGarage.get());


            } 
            else {
                if (dvlabody != null) {
                    vehicle.setMotExpiryDate(dvlabody.getMotExpiryDate());
                    vehicle.setRegistration(dvlabody.getRegistrationNumber());
                }
                Garage garage = garages.stream().findAny().orElseThrow(() -> new IllegalArgumentException("No garage associated."));
                Optional<Customer> anyCustomer = vehicle.getOwners().stream().findAny();
                anyCustomer.map(customer -> customerRepository.findById(customer.getId()).orElse(customer)).ifPresent(c -> {
                    c.getVehicles().add(vehicle);
                    vehicle.getOwners().add(c);
                });
                result = vehicleRepository.save(vehicle);
                if (anyCustomer.isPresent())
                    reminderService.updateMOTReminder(result, anyCustomer.get(), garage);
            }


        }







        if (result == null) {
            throw new BadRequestAlertException("Cannot save vehicle.", ENTITY_NAME, "vehicle-error");
        }


        return ResponseEntity.created(new URI("/api/vehicles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);


    }


    /**
     * {@code PUT  /vehicles} : Updates an existing vehicle.
     *
     * @param vehicle the vehicle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicle,
     * or with status {@code 400 (Bad Request)} if the vehicle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vehicles")
    public ResponseEntity<Vehicle> updateVehicle(@Valid @RequestBody Vehicle vehicle) throws URISyntaxException, MalformedURLException {
        log.debug("REST request to update Vehicle : {}", vehicle);
        if (vehicle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Vehicle result = null;
        try {
            DvlaVehicle body = garageAdminService.getDvlaVehicle(vehicle.getRegistration());
            if (body != null) {
                vehicle.setMotExpiryDate(body.getMotExpiryDate());
            }

            result = vehicleRepository.save(vehicle);

        } catch (HttpClientErrorException error) {
            if (error.getStatusCode().is4xxClientError() && error.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new BadRequestAlertException("Vehicle not found.", ENTITY_NAME, "vehicle-invalid");
        }
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, vehicle.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /vehicles} : get all the vehicles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicles in body.
     */
    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Vehicles");
        return vehicleRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /vehicles/:id} : get the "id" vehicle.
     *
     * @param id the id of the vehicle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable Long id) {
        log.debug("REST request to get Vehicle : {}", id);
        Optional<Vehicle> vehicle = vehicleRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(vehicle);
    }

    /**
     * {@code GET  /vehicles/:registrationNumber} : get the "id" vehicle.
     *
     * @param registrationNumber the registrationNumber of the vehicle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vehicles/dvla/{registrationNumber}")
    public ResponseEntity<DvlaVehicle> getDvlaVehicle(@PathVariable String registrationNumber) throws MalformedURLException, URISyntaxException {
        log.debug("REST request to get Vehicle : {}", registrationNumber);
        DvlaVehicle dvlaVehicle = null;
        try {
            dvlaVehicle = garageAdminService.getDvlaVehicle(registrationNumber.toUpperCase());
        } catch (HttpClientErrorException error) {
            if (error.getStatusCode().is4xxClientError() && error.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new BadRequestAlertException("Vehicle not found.", ENTITY_NAME, "vehicle-invalid");
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(dvlaVehicle));
    }

    /**
     * {@code DELETE  /vehicles/:id} : delete the "id" vehicle.
     *
     * @param id the id of the vehicle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.debug("REST request to delete Vehicle : {}", id);
        vehicleRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
