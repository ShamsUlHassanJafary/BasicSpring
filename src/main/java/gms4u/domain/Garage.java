package gms4u.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gms4u.service.dto.FileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The Garage entity.\n@author A true hipster
 */
@ApiModel(description = "The Garage entity.\n@author A true hipster")
@Entity
@Table(name = "garage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Garage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * businessName
     */
    @ApiModelProperty(value = "businessName")
    @Column(name = "business_name")
    private String businessName;

    @Column(name = "line_address_1")
    private String lineAddress1;

    @Column(name = "line_address_2")
    private String lineAddress2;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "country")
    private String country;

    @Column(name = "logo_url", length = 256)
    private String logoUrl;

    @Transient
    private FileDto file;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254)
    private String businessEmail;

    @NotNull
    @Size(min = 11, max = 13)
    @Column(name = "phone_number", length = 13, nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "garage")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "garage")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "garage")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<GarageAdmin> garageAdmins = new HashSet<>();

    @ManyToMany(mappedBy = "garages")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Vehicle> vehicles = new HashSet<>();

    @ManyToMany(mappedBy = "garages")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Customer> customers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private GarageType garageType;

    @Column(name = "vat_registered")
    private Boolean vatRegistered;

    public GarageType getGarageType() {
        return garageType;
    }

    public void setGarageType(GarageType garageType) {
        this.garageType = garageType;
    }

    public Boolean isVatRegistered() {
        return vatRegistered;
    }

    public void setVatRegistered(Boolean vatRegistered) {
        this.vatRegistered = vatRegistered;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Garage businessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getLineAddress1() {
        return lineAddress1;
    }

    public void setLineAddress1(String lineAddress1) {
        this.lineAddress1 = lineAddress1;
    }

    public Garage lineAddress1(String lineAddress1) {
        this.lineAddress1 = lineAddress1;
        return this;
    }

    public String getLineAddress2() {
        return lineAddress2;
    }

    public void setLineAddress2(String lineAddress2) {
        this.lineAddress2 = lineAddress2;
    }

    public Garage lineAddress2(String lineAddress2) {
        this.lineAddress2 = lineAddress2;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Garage city(String city) {
        this.city = city;
        return this;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Garage county(String county) {
        this.county = county;
        return this;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Garage postcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public Garage phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Garage country(String country) {
        this.country = country;
        return this;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Garage bookings(Set<Booking> bookings) {
        this.bookings = bookings;
        return this;
    }

    public Garage addBookings(Booking booking) {
        this.bookings.add(booking);
        booking.setGarage(this);
        return this;
    }

    public Garage removeBookings(Booking booking) {
        this.bookings.remove(booking);
        booking.setGarage(null);
        return this;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Garage jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Garage addJobs(Job job) {
        this.jobs.add(job);
        job.setGarage(this);
        return this;
    }

    public Garage removeJobs(Job job) {
        this.jobs.remove(job);
        job.setGarage(null);
        return this;
    }

    public Set<GarageAdmin> getGarageAdmins() {
        return garageAdmins;
    }

    public void setGarageAdmins(Set<GarageAdmin> garageAdmins) {
        this.garageAdmins = garageAdmins;
    }

    public Garage garageAdmins(Set<GarageAdmin> garageAdmins) {
        this.garageAdmins = garageAdmins;
        return this;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Garage vehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public Garage addVehicles(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        vehicle.getGarages().add(this);
        return this;
    }

    public Garage removeVehicles(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
        vehicle.getGarages().remove(this);
        return this;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Garage customers(Set<Customer> customers) {
        this.customers = customers;
        return this;
    }

    public Garage addCustomers(Customer customer) {
        this.customers.add(customer);
        customer.getGarages().add(this);
        return this;
    }

    public Garage removeCustomers(Customer customer) {
        this.customers.remove(customer);
        customer.getGarages().remove(this);
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Garage)) {
            return false;
        }
        return id != null && id.equals(((Garage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Garage{" +
            "id=" + getId() +
            ", businessName='" + getBusinessName() + "'" +
            ", lineAddress1='" + getLineAddress1() + "'" +
            ", lineAddress2='" + getLineAddress2() + "'" +
            ", city='" + getCity() + "'" +
            ", county='" + getCounty() + "'" +
            ", postcode='" + getPostcode() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
