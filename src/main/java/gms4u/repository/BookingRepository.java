package gms4u.repository;

import gms4u.domain.*;
import gms4u.domain.enumeration.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select distinct booking from Booking booking left join fetch booking.jobs",
        countQuery = "select count(distinct booking) from Booking booking")
    Page<Booking> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct booking from Booking booking left join fetch booking.jobs")
    List<Booking> findAllWithEagerRelationships();

    @Query("select booking from Booking booking left join fetch booking.jobs where booking.id =:id")
    Optional<Booking> findOneWithEagerRelationships(@Param("id") Long id);

    List<Booking> findByGarage(Garage garage);

    @Query("select distinct booking from Booking booking left join fetch booking.jobs where booking.garage = :garage and booking.status = :status")
    List<Booking> findByGarageAndStatus(@Param("garage") Garage garage, @Param("status") BookingStatus status);

    @Query("select distinct j from Booking booking left join booking.jobs j where booking.garage = :garage and booking.id = :id")
    List<Job> findJobsByBookingId(@Param("id") Long id, @Param("garage") Garage garage);


    @Query("select distinct booking from Booking booking left join fetch booking.jobs where booking.garage = :garage and booking.status in (:statuses)")
    List<Booking> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses);

    @Query(value = "select distinct booking from Booking booking left join booking.jobs where booking.garage = :garage and booking.status in (:statuses)", countQuery = "select distinct booking from Booking booking where booking.garage = :garage and booking.status in (:statuses)")
    List<Booking> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses, Pageable pageable);

    @Query(value = "select distinct booking from Booking booking left join booking.jobs where booking.garage = :garage and booking.status in (:statuses) and booking.bookingDate = :date", countQuery = "select distinct booking from Booking booking where booking.garage = :garage and booking.status in (:statuses) and booking.bookingDate = :date")
    List<Booking> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses, @Param("date") LocalDate date, Pageable pageable);


    @Query("select booking from Booking booking where booking.garage = :garage and booking.vehicle = :vehicle")
    List<Booking> findByVehicleAndGarage(@Param("garage") Garage garage, @Param("vehicle") Vehicle vehicle);

    @Query("select booking from Booking booking where booking.garage = :garage and booking.vehicle = :vehicle and booking.customer = :customer and booking.status in (:statuses)")
    List<Booking> findByVehicleAndCustomerAndGarage(@Param("garage") Garage garage, @Param("vehicle") Vehicle vehicle, @Param("customer") Customer customer, @Param("statuses") List<BookingStatus> statuses);

}
