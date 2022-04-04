package gms4u.repository;

import gms4u.domain.Customer;
import gms4u.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data  repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("select distinct booking.customer from Booking booking where booking.garage.id = :garageId")
    List<Customer> findByGarage(@Param("garageId") Long garageId);

    @Query(value = "select distinct customer from Customer customer left join fetch customer.garages",
        countQuery = "select count(distinct Customer) from Vehicle customer")
    Page<Customer> findAllWithEagerRelationships(Pageable pageable);

    Optional<Customer> findByEmailIgnoreCaseAndPhoneNumberIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(String email, String phoneNumber, String firstname, String lastname);

    @Query("select v from Customer c join c.vehicles v where c.id=:customerId")
    Set<Vehicle> getCustomerVehicles(@Param("customerId") Long customerId);

    @Query("select v from Customer c join c.vehicles v join v.garages g where c.id=:customerId and  g.id=:garageId")
    Set<Vehicle> getCustomerVehicles(@Param("customerId") Long customerId, @Param("garageId") Long garageId);
}
