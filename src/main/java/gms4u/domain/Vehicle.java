package gms4u.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * The Vehicle entity.\n@author A true hipster
 */
@ApiModel(description = "The Vehicle entity.\n@author A true hipster")
@Entity
@Table(name = "vehicle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration")
    private String registration;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "colour")
    private String colour;

    @Column(name = "mot_due")
    private LocalDate motExpiryDate;

    @OneToMany(mappedBy = "vehicle")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Booking> bookings = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @NotNull
    @JoinTable(name = "vehicle_garages",
        joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "garages_id", referencedColumnName = "id"))
    private Set<Garage> garages = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "vehicle_owners",
        joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "owners_id", referencedColumnName = "id"))
    private Set<Customer> owners = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public Vehicle registration(String registration) {
        this.registration = registration;
        return this;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getMake() {
        return make;
    }

    public Vehicle make(String make) {
        this.make = make;
        return this;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public Vehicle model(String model) {
        this.model = model;
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public Vehicle colour(String colour) {
        this.colour = colour;
        return this;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public LocalDate getMotExpiryDate() {
        return motExpiryDate;
    }

    public Vehicle motExpiryDate(LocalDate motExpiryDate) {
        this.motExpiryDate = motExpiryDate;
        return this;
    }

    public void setMotExpiryDate(LocalDate motExpiryDate) {
        this.motExpiryDate = motExpiryDate;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public Vehicle bookings(Set<Booking> bookings) {
        this.bookings = bookings;
        return this;
    }

    public Vehicle addBooking(Booking booking) {
        this.bookings.add(booking);
        booking.setVehicle(this);
        return this;
    }

    public Vehicle removeBooking(Booking booking) {
        this.bookings.remove(booking);
        booking.setVehicle(null);
        return this;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<Garage> getGarages() {
        return garages;
    }

    public Vehicle garages(Set<Garage> garages) {
        this.garages = garages;
        return this;
    }

    public Vehicle addGarages(Garage garage) {
        this.garages.add(garage);
        garage.getVehicles().add(this);
        return this;
    }

    public Vehicle removeGarages(Garage garage) {
        this.garages.remove(garage);
        garage.getVehicles().remove(this);
        return this;
    }

    public void setGarages(Set<Garage> garages) {
        this.garages = garages;
    }

    public Set<Customer> getOwners() {
        return owners;
    }

    public Vehicle owners(Set<Customer> customers) {
        this.owners = customers;
        return this;
    }

    public Vehicle addOwners(Customer customer) {
        this.owners.add(customer);
        customer.getVehicles().add(this);
        return this;
    }

    public Vehicle removeOwners(Customer customer) {
        this.owners.remove(customer);
        customer.getVehicles().remove(this);
        return this;
    }

    public void setOwners(Set<Customer> customers) {
        this.owners = customers;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehicle)) {
            return false;
        }
        return id != null && id.equals(((Vehicle) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + getId() +
            ", registration='" + getRegistration() + "'" +
            ", make='" + getMake() + "'" +
            ", model='" + getModel() + "'" +
            ", colour='" + getColour() + "'" +
            ", motDue='" + getMotExpiryDate() + "'" +
            "}";
    }
}
