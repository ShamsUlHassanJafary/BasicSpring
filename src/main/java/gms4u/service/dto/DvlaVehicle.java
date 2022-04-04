package gms4u.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class DvlaVehicle {
    private String registrationNumber;
    private String make;
    private String motStatus;
    private String colour;

//    @JsonProperty("motDue")
    @JsonFormat
        (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate motExpiryDate;

    public DvlaVehicle(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public DvlaVehicle() {
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMotStatus() {
        return motStatus;
    }

    public void setMotStatus(String motStatus) {
        this.motStatus = motStatus;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public LocalDate getMotExpiryDate() {
        return motExpiryDate;
    }

    public void setMotExpiryDate(LocalDate motExpiryDate) {
        this.motExpiryDate = motExpiryDate;
    }

    @Override
    public String toString() {
        return "DvlaVehicle{" +
            "registrationNumber='" + registrationNumber + '\'' +
            ", make='" + make + '\'' +
            ", motStatus='" + motStatus + '\'' +
            ", colour='" + colour + '\'' +
            ", motExpiryDate=" + motExpiryDate +
            '}';
    }
}
