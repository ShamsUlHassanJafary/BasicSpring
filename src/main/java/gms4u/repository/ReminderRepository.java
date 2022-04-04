package gms4u.repository;

import gms4u.domain.Customer;
import gms4u.domain.Garage;
import gms4u.domain.Reminder;
import gms4u.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data  repository for the Reminder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByGarage(Garage garage);

    List<Reminder> findByGarageAndEventDate(Garage garage, LocalDate eventDate);

}
