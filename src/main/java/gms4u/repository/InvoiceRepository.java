package gms4u.repository;

import gms4u.domain.Garage;
import gms4u.domain.Invoice;
import gms4u.domain.enumeration.BookingStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("select booking.invoice from Booking booking where booking.garage = :garage")
    List<Invoice> findByGarage(@Param("garage") Garage garage);

    @Query("select distinct invoice from Invoice invoice left join invoice.booking.jobs where invoice.booking.garage = :garage and invoice.booking.status = :status")
    List<Invoice> findByGarageAndStatus(@Param("garage") Garage garage, @Param("status") BookingStatus status);

    @Query("select distinct booking.invoice from Booking booking left join booking.jobs where booking.garage = :garage and booking.status in (:statuses)")
    List<Invoice> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses);

    @Query("select booking.invoice from Booking booking where booking.garage = :garage and booking.status in (:statuses)")
    List<Invoice> findByGarageAndStatuses(@Param("garage") Garage garage, @Param("statuses") List<BookingStatus> statuses, Sort sort);
}
