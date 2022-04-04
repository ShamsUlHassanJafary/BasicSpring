package gms4u.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gms4u.domain.enumeration.BookingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The Booking entity.\n@author A true hipster
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
@ApiModel(description = "The Booking entity.\n@author A true hipster")
@Entity
@Table(name = "booking")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * bookingDate
     */
    @ApiModelProperty(value = "bookingDate")
    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "booking_time")
    private ZonedDateTime bookingTime;

    @Column(name = "further_instruction")
    private String furtherInstruction;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "booking_jobs",
        joinColumns = @JoinColumn(name = "booking_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "jobs_id", referencedColumnName = "id"))
    private Set<Job> jobs = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "bookings", allowSetters = true)
    private Garage garage;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "bookings", allowSetters = true)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "bookings", allowSetters = true)
    private Customer customer;

    @OneToOne
    @JoinColumn(unique = true)
    private Quote quote;

    @OneToOne
    @JoinColumn(unique = true)
    private Invoice invoice;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "reference")
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Booking bookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
        return this;
    }

    public ZonedDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(ZonedDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Booking bookingTime(ZonedDateTime bookingTime) {
        this.bookingTime = bookingTime;
        return this;
    }

    public String getFurtherInstruction() {
        return furtherInstruction;
    }

    public void setFurtherInstruction(String furtherInstruction) {
        this.furtherInstruction = furtherInstruction;
    }

    public Booking furtherInstruction(String furtherInstruction) {
        this.furtherInstruction = furtherInstruction;
        return this;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Booking status(BookingStatus status) {
        this.status = status;
        return this;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Booking jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Booking addJobs(Job job) {
        this.jobs.add(job);
        job.getBookings().add(this);
        return this;
    }

    public Booking removeJobs(Job job) {
        this.jobs.remove(job);
        job.getBookings().remove(this);
        return this;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Booking garage(Garage garage) {
        this.garage = garage;
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Booking vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Booking quote(Quote quote) {
        this.quote = quote;
        return this;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Booking invoice(Invoice invoice) {
        this.invoice = invoice;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return id != null && id.equals(((Booking) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
            "id=" + getId() +
            ", bookingDate='" + getBookingDate() + "'" +
            ", bookingTime='" + getBookingTime() + "'" +
            ", furtherInstruction='" + getFurtherInstruction() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
