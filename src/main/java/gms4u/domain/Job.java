package gms4u.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The Job entity.\n@author A true hipster
 */
@ApiModel(description = "The Job entity.\n@author A true hipster")
@Entity
@Table(name = "job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * description
     */
    @ApiModelProperty(value = "description")
    @Column(name = "description", nullable = false)
    @NotNull
    private String description;

    @Column(name = "date_created")
    private ZonedDateTime dateCreated;

    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    @NotNull
    private BigDecimal price;

    @ManyToOne
    @JsonIgnoreProperties(value = "jobs", allowSetters = true)
    private Garage garage;

    @ManyToMany(mappedBy = "jobs")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Booking> bookings = new HashSet<>();

    @Column(name = "reference")
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Job description(String description) {
        this.description = description;
        return this;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Job dateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Job price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Job garage(Garage garage) {
        this.garage = garage;
        return this;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Job bookings(Set<Booking> bookings) {
        this.bookings = bookings;
        return this;
    }

    public Job addBooking(Booking booking) {
        this.bookings.add(booking);
        booking.getJobs().add(this);
        return this;
    }

    public Job removeBooking(Booking booking) {
        this.bookings.remove(booking);
        booking.getJobs().remove(this);
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        return id != null && id.equals(((Job) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
