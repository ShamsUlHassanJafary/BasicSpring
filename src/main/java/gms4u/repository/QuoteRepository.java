package gms4u.repository;

import gms4u.domain.Garage;
import gms4u.domain.Invoice;
import gms4u.domain.Quote;
import gms4u.domain.enumeration.BookingStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Quote entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query("select distinct booking.quote from Booking booking left join booking.jobs where booking.garage.id = :garageId")
    List<Quote> findByGarage(@Param("garageId") Long garageId);

    @Query("select distinct quote from Quote quote join quote.booking.jobs where quote.booking.garage = :garage")
    List<Quote> findByGarage(@Param("garage") Garage garage);

    @Query("select distinct booking.quote from Booking booking left join booking.jobs where booking.garage = :garage and booking.status = :status")
    List<Quote> findByGarageAndStatus(@Param("garage") Garage garage, @Param("status") BookingStatus status);

    @Query("select distinct booking.quote from Booking booking where booking.garage = :garage and booking.status = :status")
    List<Quote> findByGarageAndStatus(@Param("garage") Garage garage, @Param("status") BookingStatus status, Sort sort);

    @Query("select distinct booking.quote from Booking booking left join booking.jobs where booking.garage = :garage and booking.status in (:statuses)")
    List<Quote> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses);


}
