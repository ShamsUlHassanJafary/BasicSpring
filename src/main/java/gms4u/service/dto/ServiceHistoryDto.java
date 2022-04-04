package gms4u.service.dto;

import gms4u.domain.Booking;

import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryDto {

    private List<Booking> bookingHistory = new ArrayList<>();

    public ServiceHistoryDto() {
    }

    public List<Booking> getBookingHistory() {
        return bookingHistory;
    }

    public void setBookingHistory(List<Booking> bookingHistory) {
        this.bookingHistory = bookingHistory;
    }
}
