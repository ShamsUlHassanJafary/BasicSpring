package gms4u.web.rest;

import gms4u.domain.GarageType;
import gms4u.repository.GarageTypeRepository;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link gms4u.domain.GarageType}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GarageTypeResource {

    private final Logger log = LoggerFactory.getLogger(GarageTypeResource.class);

    private final GarageTypeRepository garageTypeRepository;

    public GarageTypeResource(GarageTypeRepository garageTypeRepository) {
        this.garageTypeRepository = garageTypeRepository;
    }

    /**
     * {@code GET  /garage-types} : get all the garageTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of garageTypes in body.
     */
    @GetMapping("/garage-types")
    public List<GarageType> getAllGarageTypes() {
        log.debug("REST request to get all GarageTypes");
        return garageTypeRepository.findAll();
    }

    /**
     * {@code GET  /garage-types/:id} : get the "id" garageType.
     *
     * @param id the id of the garageType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the garageType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/garage-types/{id}")
    public ResponseEntity<GarageType> getGarageType(@PathVariable Long id) {
        log.debug("REST request to get GarageType : {}", id);
        Optional<GarageType> garageType = garageTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(garageType);
    }
}
