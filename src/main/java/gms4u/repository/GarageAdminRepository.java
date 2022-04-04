package gms4u.repository;

import gms4u.domain.GarageAdmin;
import gms4u.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the GarageAdmin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GarageAdminRepository extends JpaRepository<GarageAdmin, Long> {

    Optional<GarageAdmin> findByUser(User user);

    @Query("select garageAdmin from GarageAdmin garageAdmin where garageAdmin.user.login = :login")
    Optional<GarageAdmin> findByUser(@Param("login") String login);


}
