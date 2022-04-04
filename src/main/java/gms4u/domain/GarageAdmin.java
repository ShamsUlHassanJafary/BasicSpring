package gms4u.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A GarageAdmin.
 */
@Entity
@Table(name = "garage_admin")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GarageAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("garageAdmins")
    private Garage garage;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public GarageAdmin user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Garage getGarage() {
        return garage;
    }

    public GarageAdmin garage(Garage garage) {
        this.garage = garage;
        return this;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GarageAdmin garageAdmin = (GarageAdmin) o;
        if (garageAdmin.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), garageAdmin.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "GarageAdmin{" +
            "id=" + getId() +
            "}";
    }
}
