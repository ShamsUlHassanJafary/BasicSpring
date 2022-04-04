package gms4u.service;

import gms4u.config.ApplicationProperties;
import gms4u.domain.*;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.repository.*;
import gms4u.service.dto.DvlaVehicle;
import gms4u.service.dto.FileDto;
import org.eclipse.birt.report.engine.api.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GarageAdminService {

    private final Logger log = LoggerFactory.getLogger(GarageAdminService.class);

    private final GarageAdminRepository garageAdminRepository;

    private final GarageRepository garageRepository;

    private final BookingRepository bookingRepository;

    private final QuoteRepository quoteRepository;

    private final InvoiceRepository invoiceRepository;

    private final CustomerRepository customerRepository;

    private final JobRepository jobRepository;

    private final VehicleRepository vehicleRepository;

    private final ReportingService reportingService;

    private final ReminderRepository reminderRepository;

    private final ApplicationProperties applicationProperties;


    public GarageAdminService(GarageAdminRepository garageAdminRepository, GarageRepository garageRepository, BookingRepository bookingRepository, QuoteRepository quoteRepository, InvoiceRepository invoiceRepository, CustomerRepository customerRepository, JobRepository jobRepository, VehicleRepository vehicleRepository, ReportingService reportingService, ReminderRepository reminderRepository, ApplicationProperties applicationProperties) {
        this.garageAdminRepository = garageAdminRepository;
        this.garageRepository = garageRepository;
        this.bookingRepository = bookingRepository;
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.jobRepository = jobRepository;
        this.vehicleRepository = vehicleRepository;
        this.reportingService = reportingService;
        this.reminderRepository = reminderRepository;
        this.applicationProperties = applicationProperties;
    }

    public GarageAdmin registerUserAsGarageAdmin(User user) {
        Optional<GarageAdmin> byUser = garageAdminRepository.findByUser(user);

        if (byUser.isPresent()) {
            return garageAdminRepository.save(byUser.get());
        } else {
            GarageAdmin garageAdmin = new GarageAdmin();
            garageAdmin.setUser(user);
            return garageAdminRepository.save(garageAdmin);
        }
    }

    public void deleteGarageAdmin(GarageAdmin garageAdmin) {
        garageAdminRepository.delete(garageAdmin);
    }

    public Optional<GarageAdmin> getGarageAdminByUser(User user) {
        return garageAdminRepository.findByUser(user);
    }

    public Optional<Garage> getGarageFromUser(User user) {
        Optional<GarageAdmin> garageAdminByUser = getGarageAdminByUser(user);

        return getGarageByAdminUser(garageAdminByUser);
    }

    public Optional<Garage> getGarageByAdminUser(Optional<GarageAdmin> garageAdminByUser) {
        if (garageAdminByUser.isPresent()) {
            Garage garage = garageAdminByUser.get().getGarage();
            return Optional.of(garage);
        }
        return Optional.empty();
    }

    public void registerGarageAdminToGarage(Garage garage, GarageAdmin garageAdmin) {
        if (garage == null)
            throw new IllegalArgumentException("garage is null");

        garageAdmin.setGarage(garage);
        garageAdminRepository.save(garageAdmin);

    }


    public List<Customer> getGarageAllCustomers(Long garageId) {
        return garageRepository.findCustomersByGarage(garageId);
    }

    public DvlaVehicle getDvlaVehicle(String registration) throws MalformedURLException, URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-api-key", applicationProperties.getDvla().getKey());
        HttpEntity httpEntity = new HttpEntity(new DvlaVehicle(registration), headers);
        ResponseEntity<DvlaVehicle> dvlaVehicleResponseEntity = null;
        DvlaVehicle body = null;

        String url = applicationProperties.getDvla().getUrl();
        if (url != null) {

            try{
                dvlaVehicleResponseEntity = restTemplate.postForEntity(url, httpEntity, DvlaVehicle.class);
                if (dvlaVehicleResponseEntity.getStatusCode().is2xxSuccessful()) {
                    body = dvlaVehicleResponseEntity.getBody();
                }

            }catch(Exception e){
                body = null;
            }
          

            
            
        }

        return body;


    }

    public List<Vehicle> getGarageAllVehicles(Long garageId) {


        return garageRepository.findVehiclesByGarage(garageId);
    }


    public List<Booking> getGarageAllBookingsWithStatuses(Long garageId, List<BookingStatus> statuses) {
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses).stream().filter(b -> b.getBookingDate() != null && b.getBookingTime() != null).collect(Collectors.toList());
    }

    public List<Booking> getGarageAllBookingsWithStatuses(Long garageId, List<BookingStatus> statuses, Pageable pageable) {
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses, pageable).stream().filter(b -> b.getBookingDate() != null && b.getBookingTime() != null).collect(Collectors.toList());
    }

    public List<Booking> getGarageTodayBookingsWithStatuses(Long garageId, List<BookingStatus> statuses, LocalDate date, Pageable pageable) {
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses, date, pageable).stream().filter(b -> b.getBookingDate() != null && b.getBookingTime() != null).collect(Collectors.toList());
    }


    public List<Booking> getGarageAllBookingsWithStatus(Long garageId, BookingStatus status) {
        return bookingRepository.findByGarageAndStatus(getGarage(garageId), status).stream().filter(b -> b.getBookingDate() != null && b.getBookingTime() != null).collect(Collectors.toList());
    }

    public List<Booking> getGarageAllBookings(Long garageId) {
        List<BookingStatus> statuses = Arrays.stream(BookingStatus.values()).filter(bookingStatus -> !bookingStatus.equals(BookingStatus.PENDING)).collect(Collectors.toList());
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses).stream().filter(b -> b.getBookingDate() != null && b.getBookingTime() != null).collect(Collectors.toList());
    }

    public List<Booking> getGarageAllServiceHistory(Long garageId) {
        List<BookingStatus> statuses = Arrays.stream(BookingStatus.values()).filter(bookingStatus -> bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.INVOICED)).collect(Collectors.toList());
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses).stream().collect(Collectors.toList());
    }

    public List<Booking> getFilteredServiceHistory(Long garageId, Long customerId, Long vehicleId) {
        List<BookingStatus> statuses = Arrays.stream(BookingStatus.values()).filter(bookingStatus -> bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.INVOICED)).collect(Collectors.toList());

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("No vehicle found."));

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("No customer found."));

        return bookingRepository.findByVehicleAndCustomerAndGarage(getGarage(garageId), vehicle, customer, statuses).stream().collect(Collectors.toList());
    }

    public List<Booking> getGarageAllServiceHistory(Long garageId, Pageable pageable) {
        List<BookingStatus> statuses = Arrays.stream(BookingStatus.values()).filter(bookingStatus -> bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.INVOICED)).collect(Collectors.toList());
        return bookingRepository.findByGarageAndStatuses(getGarage(garageId), statuses, pageable).stream().collect(Collectors.toList());
    }

    public List<Quote> getGarageAllQuotations(Long garageId) {
        return quoteRepository.findByGarageAndStatus(getGarage(garageId), BookingStatus.PENDING);
    }

    public List<Quote> getGarageAllQuotations(Long garageId, Sort sort) {
        return quoteRepository.findByGarageAndStatus(getGarage(garageId), BookingStatus.PENDING, sort);
    }

    public List<Invoice> getGarageAllInvoices(Long garageId) {
        return invoiceRepository.findByGarageAndStatuses(getGarage(garageId), Arrays.asList(BookingStatus.INVOICED));
    }

    public List<Invoice> getGarageAllInvoices(Long garageId, Sort sort) {
        return invoiceRepository.findByGarageAndStatuses(getGarage(garageId), Arrays.asList(BookingStatus.INVOICED), sort);
    }

    public List<Reminder> getGarageAllReminders(Long garageId, LocalDate eventDate) {
        if (eventDate != null)
            return reminderRepository.findByGarageAndEventDate(getGarage(garageId), eventDate);

        return reminderRepository.findByGarage(getGarage(garageId));
    }

    public void getOrSaveQuote(Booking booking) {
        Quote quote = booking.getQuote();

        if (quote != null) {
            if (quote.getId() != null) {
                Optional<Quote> optionalQuote = quoteRepository.findById(quote.getId());
                Quote result = optionalQuote.orElseThrow(() -> new IllegalArgumentException("Cannot find quote with id:" + quote.getId()));
                booking.setQuote(result);
                result.setBooking(booking);

            } else {
                Quote savedQuote = quoteRepository.save(quote);
                booking.setQuote(savedQuote);
                savedQuote.setBooking(booking);
            }
        }

    }

    public void getOrSaveVehicle(Reminder reminder) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByRegistration(reminder.getVehicle().getRegistration());

        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();

            if (!vehicle.getGarages().contains(reminder.getGarage())) {
                vehicle.addGarages(reminder.getGarage());
            }

            if (!vehicle.getOwners().contains(reminder.getCustomer())) {
                vehicle.addOwners(reminder.getCustomer());
            }


            reminder.setVehicle(vehicle);
        }
    }

    public void getOrSaveCustomer(Reminder reminder) {
        Customer customer = reminder.getCustomer();
        Optional<Customer> optionalCustomer = customerRepository.
            findByEmailIgnoreCaseAndPhoneNumberIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
                customer.getEmail(), customer.getPhoneNumber(), customer.getFirstName(), customer.getLastName());
        if (optionalCustomer.isPresent()) {

            Customer theCustomer = optionalCustomer.get();
            if (!theCustomer.getGarages().contains(reminder.getGarage())) {
                theCustomer.addGarages(reminder.getGarage());
            }

            if (!theCustomer.getVehicles().contains(reminder.getVehicle())) {
                theCustomer.addVehicles(reminder.getVehicle());
            }

            reminder.setCustomer(theCustomer);
        }
    }


    public void getOrSaveVehicle(Booking booking) throws MalformedURLException, URISyntaxException {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByRegistration(booking.getVehicle().getRegistration());

        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();

            Garage garage = booking.getGarage() != null && booking.getGarage().getId() != null ? garageRepository.findById(booking.getGarage().getId()).orElse(booking.getGarage()) : null;

            if (garage != null && !vehicle.getGarages().contains(garage)) {
                vehicle.addGarages(booking.getGarage());
            }

            if (!vehicle.getOwners().contains(booking.getCustomer())) {
                vehicle.addOwners(booking.getCustomer());
            }

            DvlaVehicle dvlaVehicle = getDvlaVehicle(vehicle.getRegistration());
            if (dvlaVehicle != null)
                vehicle.setMotExpiryDate(dvlaVehicle.getMotExpiryDate());

            booking.setVehicle(vehicle);
        }
    }

    public void getOrSaveCustomer(Booking booking) {
        Customer customer = booking.getCustomer();
        Optional<Customer> optionalCustomer = customerRepository.
            findByEmailIgnoreCaseAndPhoneNumberIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
                customer.getEmail(), customer.getPhoneNumber(), customer.getFirstName(), customer.getLastName());
        if (optionalCustomer.isPresent()) {

            Customer theCustomer = optionalCustomer.get();
            Garage garage = booking.getGarage() != null && booking.getGarage().getId() != null ? garageRepository.findById(booking.getGarage().getId()).orElse(booking.getGarage()) : null;

            if (garage != null && !theCustomer.getGarages().contains(garage)) {
                theCustomer.addGarages(garage);
            }

            if (!theCustomer.getVehicles().contains(booking.getVehicle())) {
                theCustomer.addVehicles(booking.getVehicle());
            }

            booking.setCustomer(theCustomer);
        }
    }

    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll();
    }

    public List<Job> getGarageAllJobs(Long garageId) {

        return jobRepository.findByGarage(getGarage(garageId));
    }

    private Garage getGarage(Long garageId) {
        return garageRepository.findById(garageId).orElseThrow(() -> new IllegalArgumentException("Could not find garage with id " + garageId));
    }

    public List<Job> getJobsByBookingId(Long bookingId, Long garageId) {
        return bookingRepository.findJobsByBookingId(bookingId, getGarage(garageId));
    }

    public FileDto createInvoice(Booking booking) throws FileNotFoundException {

        if (booking.getId() != null) {
            Optional<Booking> byBookingId = bookingRepository.findById(booking.getId());
            if (byBookingId.isPresent()) {
                Booking b = byBookingId.get();
                if (b.getStatus().equals(BookingStatus.INVOICED))
                    saveInvoiceIfNew(b);

                try {
                    //Report here
                    return reportingService.generateInvoice(b);
                } catch (EngineException | IOException e) {
                    throw new FileNotFoundException("Problem with generating report for booking: " + booking.getId());
                }

            }
        }

        return new FileDto();

    }

    public FileDto createServiceHistoryReport(Long customerId, Long vehicleId, Long garageId) throws EngineException, IOException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));
        List<BookingStatus> statuses = Arrays.stream(BookingStatus.values()).filter(bookingStatus -> bookingStatus.equals(BookingStatus.COMPLETED) || bookingStatus.equals(BookingStatus.INVOICED)).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByVehicleAndCustomerAndGarage(getGarage(garageId), vehicle, customer, statuses);
        return reportingService.generateServiceHistoryReport(getGarage(garageId), customer, vehicle, bookings);
    }

    private void saveInvoiceIfNew(Booking booking) {
        Invoice invoice = booking.getInvoice();
        if (invoice == null || invoice.getId() == null) {
            Invoice newInvoice = new Invoice();
            newInvoice.setInvoiceDate(ZonedDateTime.now());
            newInvoice.setInvoiceTotal(booking.getQuote().getQuoteTotal());
            newInvoice.setBooking(booking);
            invoice = invoiceRepository.save(newInvoice);
            booking.setInvoice(invoice);
        } else {
            Optional<Invoice> byId = invoiceRepository.findById(invoice.getId());
            if (byId.isPresent()) {
                invoice = byId.get();
                booking.setInvoice(invoice);
            }
        }

    }

    public void deleteVehicleFromGarage(Long vehicleId, Long garageId) {
        vehicleRepository.findById(vehicleId).ifPresent(vehicle -> {
            Garage garage = getGarage(garageId).removeVehicles(vehicle);
            garageRepository.save(garage);
        });

    }

    public void deleteBookingFromQuote(Long bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getQuote() != null)
                quoteRepository.findById(booking.getQuote().getId()).ifPresent(quote -> {
                    quote.setBooking(null);
                    quoteRepository.save(quote);
                });
        });

    }

    public void deleteQuote(Long quoteId) {
        quoteRepository.findById(quoteId).ifPresent(quote -> {
            Booking booking = quote.getBooking();
            if (booking != null) {
                deleteJobsFromBooking(booking.getId());
                bookingRepository.delete(booking);
            }

            quoteRepository.deleteById(quoteId);
        });

    }

    public void deleteInvoice(Long invoiceId) {
        invoiceRepository.findById(invoiceId).ifPresent(invoice -> {
            if (invoice.getBooking() != null)
                deleteBooking(invoice.getBooking().getId());
            invoiceRepository.delete(invoice);
        });
    }

    public void deleteBooking(Long bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            Quote quote = booking.getQuote();
            if (quote != null) {
                quoteRepository.deleteById(quote.getId());
            }
            deleteJobsFromBooking(booking.getId());
            bookingRepository.deleteById(bookingId);
        });


    }

    public void deleteJobsFromQuote(Long quoteId) {
        quoteRepository.findById(quoteId).ifPresent(quote -> {
            Booking booking = quote.getBooking();
            deleteJobsFromBooking(booking.getId());
        });
    }

    public void deleteJobsFromInvoice(Long invoiceId) {
        invoiceRepository.findById(invoiceId).ifPresent(invoice -> {
            Booking booking = invoice.getBooking();
            deleteBookingFromQuote(booking.getId());
            deleteJobsFromBooking(booking.getId());
        });
    }

    public void deleteJobsFromBooking(Long bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getJobs() != null && !booking.getJobs().isEmpty()) {
                Set<Job> collect = booking.getJobs().stream().filter(job -> job.getGarage() == null).collect(Collectors.toSet());
                booking.getJobs().clear();
                if (!collect.isEmpty()) {
                    jobRepository.deleteAll(collect);
                }
            }

            bookingRepository.save(booking);
        });
    }

    public void deleteCustomerFromGarage(Long customerId, Long garageId) {
        customerRepository.findById(customerId).ifPresent(customer -> {
            Garage garage = getGarage(garageId).removeCustomers(customer);
            garageRepository.save(garage);
        });

    }

    public void saveCustomJobs(Booking booking) {
        List<Job> jobs = booking.getJobs().stream().filter(job -> job.getId() == null).collect(Collectors.toList());
        jobRepository.saveAll(jobs);

    }

    public void handleBookingTime(Booking booking) {
        if (booking.getBookingDate() != null && booking.getBookingTime() != null) {
            LocalTime bookingTime = booking.getBookingTime().toLocalTime();
            LocalDate bookingDate = booking.getBookingDate();
            booking.setBookingTime(ZonedDateTime.of(bookingDate, bookingTime, ZoneId.of("Europe/London")));

        }

    }

    public String generateJobReference(Job job) {
        Garage garage = job.getGarage();

        int size = jobRepository.findByGarage(garage).size();
        StringBuilder referenceBuilder = new StringBuilder();
//        TODO: To be improved later.
//        StringBuilder referenceBuilder = new StringBuilder("GMS4U");
//        referenceBuilder.append("-");
//        referenceBuilder.append(garage.getId());
//        referenceBuilder.append("-");
//        referenceBuilder.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()));
//        referenceBuilder.append("-");
        referenceBuilder.append(size + 1);
        return referenceBuilder.toString();
    }


    public String generateReminderReference(Reminder reminder) {
        Garage garage = reminder.getGarage();

        int size = reminderRepository.findByGarage(garage).size();
        StringBuilder referenceBuilder = new StringBuilder();
//        TODO: To be improved later.
//        StringBuilder referenceBuilder = new StringBuilder("GMS4U");
//        referenceBuilder.append("-");
//        referenceBuilder.append(garage.getId());
//        referenceBuilder.append("-");
//        referenceBuilder.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()));
//        referenceBuilder.append("-");
        referenceBuilder.append(size + 1);
        return referenceBuilder.toString();
    }

    public String generateBookingReference(Booking booking) {
        Garage garage = booking.getGarage();

        int size = bookingRepository.findByGarage(garage).size();
        StringBuilder bookingReferenceBuilder = new StringBuilder();
//        TODO: To be improved later.
//        StringBuilder bookingReferenceBuilder = new StringBuilder("GMS4U");
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(garage.getId());
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()));
//        bookingReferenceBuilder.append("-");
        bookingReferenceBuilder.append(size + 1);
        return bookingReferenceBuilder.toString();
    }

    public String generateInvoiceReference(Booking booking) {
        Garage garage = booking.getGarage();
        int size = invoiceRepository.findByGarage(garage).size();
        StringBuilder bookingReferenceBuilder = new StringBuilder();
//        TODO: To be improved later.
//        StringBuilder bookingReferenceBuilder = new StringBuilder("GMS4U");
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(garage.getId());
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()));
//        bookingReferenceBuilder.append("-");
        bookingReferenceBuilder.append(size + 1);
        return bookingReferenceBuilder.toString();
    }

    public String generateQuoteReference(Booking booking) {
        Garage garage = booking.getGarage();
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.COMPLETED, BookingStatus.IN_PROGRESS, BookingStatus.INVOICED);
        int size = quoteRepository.findByGarage(garage).size();
        StringBuilder bookingReferenceBuilder = new StringBuilder();
//        TODO: To be improved later.
//        StringBuilder bookingReferenceBuilder = new StringBuilder("GMS4U");
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(garage.getId());
//        bookingReferenceBuilder.append("-");
//        bookingReferenceBuilder.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()));
//        bookingReferenceBuilder.append("-");
        bookingReferenceBuilder.append(size + 1);
        return bookingReferenceBuilder.toString();
    }
}
