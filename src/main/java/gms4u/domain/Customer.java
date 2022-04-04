package gms4u.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The Customer entity.\n@author A true hipster
 */
@ApiModel(description = "The Customer entity.\n@author A true hipster")
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * keepData
     */
    @ApiModelProperty(value = "keepData")
    @Column(name = "has_data_keep_consent")
    private Boolean hasDataKeepConsent;

    @Column(name = "has_marketing_content")
    private Boolean hasMarketingContent;

    @Column(name = "has_notification_content")
    private Boolean hasNotificationContent;

    @Size(min = 11, max = 13)
    @Column(name = "phone_number", length = 13)
    private String phoneNumber;

    @NotNull
    @Size(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @NotNull
    @Size(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @NotNull
    @Size(min = 5, max = 254)
    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Booking> bookings = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "customer_garages",
        joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "garages_id", referencedColumnName = "id"))
    private Set<Garage> garages = new HashSet<>();

    @ManyToMany(mappedBy = "owners")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Vehicle> vehicles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isHasDataKeepConsent() {
        return hasDataKeepConsent;
    }

    public Customer hasDataKeepConsent(Boolean hasDataKeepConsent) {
        this.hasDataKeepConsent = hasDataKeepConsent;
        return this;
    }

    public void setHasDataKeepConsent(Boolean hasDataKeepConsent) {
        this.hasDataKeepConsent = hasDataKeepConsent;
    }

    public Boolean isHasMarketingContent() {
        return hasMarketingContent;
    }

    public Customer hasMarketingContent(Boolean hasMarketingContent) {
        this.hasMarketingContent = hasMarketingContent;
        return this;
    }

    public void setHasMarketingContent(Boolean hasMarketingContent) {
        this.hasMarketingContent = hasMarketingContent;
    }

    public Boolean isHasNotificationContent() {
        return hasNotificationContent;
    }

    public Customer hasNotificationContent(Boolean hasNotificationContent) {
        this.hasNotificationContent = hasNotificationContent;
        return this;
    }

    public void setHasNotificationContent(Boolean hasNotificationContent) {
        this.hasNotificationContent = hasNotificationContent;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Customer phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public Customer firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public Customer email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public Customer user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public Customer bookings(Set<Booking> bookings) {
        this.bookings = bookings;
        return this;
    }

    public Customer addBookings(Booking booking) {
        this.bookings.add(booking);
        booking.setCustomer(this);
        return this;
    }

    public Customer removeBookings(Booking booking) {
        this.bookings.remove(booking);
        booking.setCustomer(null);
        return this;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<Garage> getGarages() {
        return garages;
    }

    public Customer garages(Set<Garage> garages) {
        this.garages = garages;
        return this;
    }

    public Customer addGarages(Garage garage) {
        this.garages.add(garage);
        garage.getCustomers().add(this);
        return this;
    }

    public Customer removeGarages(Garage garage) {
        this.garages.remove(garage);
        garage.getCustomers().remove(this);
        return this;
    }

    public void setGarages(Set<Garage> garages) {
        this.garages = garages;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public Customer vehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public Customer addVehicles(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        vehicle.getOwners().add(this);
        return this;
    }

    public Customer removeVehicles(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
        vehicle.getOwners().remove(this);
        return this;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", hasDataKeepConsent='" + isHasDataKeepConsent() + "'" +
            ", hasMarketingContent='" + isHasMarketingContent() + "'" +
            ", hasNotificationContent='" + isHasNotificationContent() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
