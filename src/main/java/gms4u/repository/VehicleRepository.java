package gms4u.repository;

import gms4u.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Vehicle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistration(String registration);

    @Query("select distinct booking.vehicle from Booking booking where booking.garage.id = :garageId")
    List<Vehicle> findByGarage(@Param("garageId") Long garageId);

    @Query(value = "select distinct vehicle from Vehicle vehicle left join fetch vehicle.garages",
        countQuery = "select count(distinct vehicle) from Vehicle vehicle")
    Page<Vehicle> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct vehicle from Vehicle vehicle left join fetch vehicle.garages")
    List<Vehicle> findAllWithEagerRelationships();

    @Query("select vehicle from Vehicle vehicle left join fetch vehicle.garages where vehicle.id =:id")
    Optional<Vehicle> findOneWithEagerRelationships(@Param("id") Long id);
}
