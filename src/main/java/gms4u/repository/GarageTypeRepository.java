package gms4u.repository;

import gms4u.domain.GarageType;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the GarageType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GarageTypeRepository extends JpaRepository<GarageType, Long> {
}
