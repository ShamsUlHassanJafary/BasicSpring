package gms4u.web.rest;

import gms4u.domain.Reminder;
import gms4u.repository.ReminderRepository;
import gms4u.service.GarageAdminService;
import gms4u.service.ReminderService;
import gms4u.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link gms4u.domain.Reminder}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReminderResource {

    private static final String ENTITY_NAME = "reminder";
    private final Logger log = LoggerFactory.getLogger(ReminderResource.class);
    private final ReminderRepository reminderRepository;
    private final ReminderService reminderService;
    private final GarageAdminService garageAdminService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ReminderResource(ReminderRepository reminderRepository, ReminderService reminderService, GarageAdminService garageAdminService) {
        this.reminderRepository = reminderRepository;
        this.reminderService = reminderService;
        this.garageAdminService = garageAdminService;
    }

    /**
     * {@code POST  /reminders} : Create a new reminder.
     *
     * @param reminder the reminder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reminder, or with status {@code 400 (Bad Request)} if the reminder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reminders")
    public ResponseEntity<Reminder> createReminder(@Valid @RequestBody Reminder reminder) throws URISyntaxException, SchedulerException {
        log.debug("REST request to save Reminder : {}", reminder);
        if (reminder.getId() != null) {
            throw new BadRequestAlertException("A new reminder cannot already have an ID", ENTITY_NAME, "idexists");
        }

        garageAdminService.getOrSaveCustomer(reminder);

        garageAdminService.getOrSaveVehicle(reminder);

        if (reminder.getReference() == null)
            reminder.setReference(garageAdminService.generateReminderReference(reminder));

        Reminder result = reminderRepository.save(reminder);

        reminderService.scheduleReminder(result);

        return ResponseEntity.created(new URI("/api/reminders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reminders} : Updates an existing reminder.
     *
     * @param reminder the reminder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reminder,
     * or with status {@code 400 (Bad Request)} if the reminder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reminder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reminders")
    public ResponseEntity<Reminder> updateReminder(@Valid @RequestBody Reminder reminder) throws URISyntaxException {
        log.debug("REST request to update Reminder : {}", reminder);
        if (reminder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        garageAdminService.getOrSaveCustomer(reminder);

        garageAdminService.getOrSaveVehicle(reminder);

        if (reminder.getReference() == null)
            reminder.setReference(garageAdminService.generateReminderReference(reminder));

        Reminder result = reminderRepository.save(reminder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reminder.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /reminders} : get all the reminders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reminders in body.
     */
    @GetMapping("/reminders")
    public List<Reminder> getAllReminders() {
        log.debug("REST request to get all Reminders");
        return reminderRepository.findAll();
    }

    /**
     * {@code GET  /reminders/:id} : get the "id" reminder.
     *
     * @param id the id of the reminder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reminder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reminders/{id}")
    public ResponseEntity<Reminder> getReminder(@PathVariable Long id) {
        log.debug("REST request to get Reminder : {}", id);
        Optional<Reminder> reminder = reminderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reminder);
    }

    /**
     * {@code DELETE  /reminders/:id} : delete the "id" reminder.
     *
     * @param id the id of the reminder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        log.debug("REST request to delete Reminder : {}", id);
        reminderRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
