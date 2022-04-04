package gms4u.repository;

import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.GarageAdmin;
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
 * Spring Data  repository for the Garage entity.
 */
@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

    @Query(value = "select distinct garage from Garage garage left join fetch garage.garageAdmins",
        countQuery = "select count(distinct garage) from Garage garage")
    Page<Garage> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct garage from Garage garage left join fetch garage.garageAdmins")
    List<Garage> findAllWithEagerRelationships();

    @Query("select garage from Garage garage left join fetch garage.garageAdmins where garage.id =:id")
    Optional<Garage> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select distinct garage from Garage garage left join fetch garage.garageAdmins ga where ga = :garageAdmin")
    List<Garage> findByGarageAdmin(@Param("garageAdmin") GarageAdmin garageAdmin);

    @Query("select distinct garage.vehicles from Garage garage where garage.id = :garageId")
    List<Vehicle> findVehiclesByGarage(@Param("garageId") Long garageId);

    @Query("select distinct garage.customers from Garage garage where garage.id = :garageId")
    List<Customer> findCustomersByGarage(@Param("garageId") Long garageId);
}
