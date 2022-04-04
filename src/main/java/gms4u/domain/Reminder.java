package gms4u.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * A Reminder.
 */
@Entity
@Table(name = "reminder")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @NotNull
    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;

    @Column(name = "second_alert_date")
    private LocalDate secondAlertDate;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled;

    @NotNull
    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled;

    @Column(name = "alert_time")
    private ZonedDateTime alertTime;

    @Column(name = "second_alert_time")
    private ZonedDateTime secondAlertTime;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "reminders", allowSetters = true)
    private Customer customer;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "reminders", allowSetters = true)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "reminders", allowSetters = true)
    private Garage garage;

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

    public Reminder description(String description) {
        this.description = description;
        return this;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Reminder eventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Reminder enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDate getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(LocalDate alertDate) {
        this.alertDate = alertDate;
    }

    public Reminder alertDate(LocalDate alertDate) {
        this.alertDate = alertDate;
        return this;
    }

    public LocalDate getSecondAlertDate() {
        return secondAlertDate;
    }

    public void setSecondAlertDate(LocalDate secondAlertDate) {
        this.secondAlertDate = secondAlertDate;
    }

    public Reminder secondAlertDate(LocalDate secondAlertDate) {
        this.secondAlertDate = secondAlertDate;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Reminder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public Boolean isEmailEnabled() {
        return emailEnabled;
    }

    public Reminder emailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
        return this;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean isSmsEnabled() {
        return smsEnabled;
    }

    public Reminder smsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
        return this;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public ZonedDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(ZonedDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public Reminder alertTime(ZonedDateTime alertTime) {
        this.alertTime = alertTime;
        return this;
    }

    public ZonedDateTime getSecondAlertTime() {
        return secondAlertTime;
    }

    public void setSecondAlertTime(ZonedDateTime secondAlertTime) {
        this.secondAlertTime = secondAlertTime;
    }

    public Reminder secondAlertTime(ZonedDateTime secondAlertTime) {
        this.secondAlertTime = secondAlertTime;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Reminder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Reminder vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Reminder garage(Garage garage) {
        this.garage = garage;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reminder)) {
            return false;
        }
        return id != null && id.equals(((Reminder) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reminder{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", eventDate='" + getEventDate() + "'" +
            ", enabled='" + isEnabled() + "'" +
            ", alertDate='" + getAlertDate() + "'" +
            ", secondAlertDate='" + getSecondAlertDate() + "'" +
            ", comment='" + getComment() + "'" +
            ", emailEnabled='" + isEmailEnabled() + "'" +
            ", smsEnabled='" + isSmsEnabled() + "'" +
            ", alertTime='" + getAlertTime() + "'" +
            ", secondAlertTime='" + getSecondAlertTime() + "'" +
            "}";
    }
}
