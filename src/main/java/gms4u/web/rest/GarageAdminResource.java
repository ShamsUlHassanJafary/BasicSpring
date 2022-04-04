package gms4u.web.rest;

import gms4u.config.Constants;
import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.GarageAdmin;
import gms4u.repository.GarageAdminRepository;
import gms4u.repository.GarageRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.UserService;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link gms4u.domain.GarageAdmin}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GarageAdminResource {

    private final Logger log = LoggerFactory.getLogger(GarageAdminResource.class);

    private static final String ENTITY_NAME = "garageAdmin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GarageAdminRepository garageAdminRepository;

    private final GarageRepository garageRepository;

    private final UserService userService;

    private final GarageAdminService garageAdminService;

    public GarageAdminResource(GarageAdminRepository garageAdminRepository, GarageRepository garageRepository, UserService userService, GarageAdminService garageAdminService) {
        this.garageAdminRepository = garageAdminRepository;
        this.garageRepository = garageRepository;
        this.userService = userService;
        this.garageAdminService = garageAdminService;
    }

    /**
     * {@code POST  /garage-admins} : Create a new garageAdmin.
     *
     * @param garageAdmin the garageAdmin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new garageAdmin, or with status {@code 400 (Bad Request)} if the garageAdmin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/garage-admins")
    public ResponseEntity<GarageAdmin> createGarageAdmin(@RequestBody GarageAdmin garageAdmin) throws URISyntaxException {
        log.debug("REST request to save GarageAdmin : {}", garageAdmin);
        if (garageAdmin.getId() != null) {
            throw new BadRequestAlertException("A new garageAdmin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GarageAdmin result = garageAdminRepository.save(garageAdmin);
        return ResponseEntity.created(new URI("/api/garage-admins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /garage-admins} : Updates an existing garageAdmin.
     *
     * @param garageAdmin the garageAdmin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated garageAdmin,
     * or with status {@code 400 (Bad Request)} if the garageAdmin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the garageAdmin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/garage-admins")
    public ResponseEntity<GarageAdmin> updateGarageAdmin(@RequestBody GarageAdmin garageAdmin) throws URISyntaxException {
        log.debug("REST request to update GarageAdmin : {}", garageAdmin);
        if (garageAdmin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GarageAdmin result = garageAdminRepository.save(garageAdmin);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, garageAdmin.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /garage-admins} : get all the garageAdmins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of garageAdmins in body.
     */
    @GetMapping("/garage-admins")
    public List<GarageAdmin> getAllGarageAdmins() {
        log.debug("REST request to get all GarageAdmins");
        return garageAdminRepository.findAll();
    }

    /**
     * {@code GET  /garage-admins/:id} : get the "id" garageAdmin.
     *
     * @param id the id of the garageAdmin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the garageAdmin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/garage-admins/{id}")
    public ResponseEntity<GarageAdmin> getGarageAdmin(@PathVariable Long id) {
        log.debug("REST request to get GarageAdmin : {}", id);
        Optional<GarageAdmin> garageAdmin = garageAdminRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(garageAdmin);
    }

    @GetMapping("/garage-admins/user/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<GarageAdmin> getGarageAdminByUser(@PathVariable String login) {
        log.debug("REST request to get GarageAdmin from login: {}", login);
        Optional<GarageAdmin> byUser = garageAdminRepository.findByUser(login);
        return ResponseUtil.wrapOrNotFound(byUser);
    }

    @GetMapping("/garage-admins/user/{login:" + Constants.LOGIN_REGEX + "}/garages")
    public List<Garage> getGaragesByUser(@PathVariable String login) {
        log.debug("REST request to get GarageAdmin from login: {}", login);
        Optional<GarageAdmin> byUser = garageAdminRepository.findByUser(login);
        if (byUser.isPresent())
            return garageRepository.findByGarageAdmin(byUser.get());
        else return new ArrayList<>();
    }

    @GetMapping("/garage-admins/user/{login:" + Constants.LOGIN_REGEX + "}/garages/{garageId}/customers")
    public List<Customer> getCustomersByGarageAdminUser(@PathVariable String login, @PathVariable String garageId) {
        log.debug("REST request to get GarageAdmin from login: {}", login);
        Optional<GarageAdmin> byUser = garageAdminRepository.findByUser(login);
        Optional<Garage> garageByAdminUser = garageAdminService.getGarageByAdminUser(byUser);
        if (byUser.isPresent() && garageByAdminUser.isPresent())
            return garageAdminService.getGarageAllCustomers(garageByAdminUser.get().getId());
        else return new ArrayList<>();
    }

    /**
     * {@code DELETE  /garage-admins/:id} : delete the "id" garageAdmin.
     *
     * @param id the id of the garageAdmin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/garage-admins/{id}")
    public ResponseEntity<Void> deleteGarageAdmin(@PathVariable Long id) {
        log.debug("REST request to delete GarageAdmin : {}", id);
        garageAdminRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
