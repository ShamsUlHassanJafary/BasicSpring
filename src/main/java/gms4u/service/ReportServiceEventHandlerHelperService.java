package gms4u.service;

import gms4u.domain.Job;
import gms4u.repository.BookingRepository;
import gms4u.repository.GarageRepository;
import gms4u.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportServiceEventHandlerHelperService {

    private final Logger log = LoggerFactory.getLogger(ReportServiceEventHandlerHelperService.class);

    private ReportingService reportingService;
    private VehicleRepository vehicleRepository;
    private BookingRepository bookingRepository;
    private GarageRepository garageRepository;

    public List<Job> getJobsByBookingId(Long bookingId, Long garageId) {

        return bookingRepository.findJobsByBookingId(bookingId, garageRepository.getOne(garageId));

    }

    public ReportingService getReportingService() {
        return reportingService;
    }

    public void setReportingService(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public VehicleRepository getVehicleRepository() {
        return vehicleRepository;
    }

    public void setVehicleRepository(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public GarageRepository getGarageRepository() {
        return garageRepository;
    }

    public void setGarageRepository(GarageRepository garageRepository) {
        this.garageRepository = garageRepository;
    }
}
