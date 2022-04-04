package gms4u.service;

import gms4u.config.ApplicationProperties;
import gms4u.config.scheduler.job.BookingJob;
import gms4u.config.scheduler.job.ReminderJob;
import gms4u.domain.*;
import gms4u.repository.ReminderRepository;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
@Transactional
public class ReminderService {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final ZoneId LONDON_TIMEZONE = ZoneId.of("Europe/London");

    private final SMSService smsService;

    private final MailService mailService;

    private final Logger log = LoggerFactory.getLogger(ReminderService.class);


    private final ApplicationProperties applicationProperties;
    private final Scheduler scheduler;
    private final ReminderRepository reminderRepository;

    public ReminderService(SMSService smsService, MailService mailService, ApplicationProperties applicationProperties, Scheduler scheduler, ReminderRepository reminderRepository) {
        this.smsService = smsService;
        this.mailService = mailService;
        this.applicationProperties = applicationProperties;
        this.scheduler = scheduler;
        this.reminderRepository = reminderRepository;
    }

    public void scheduleReminder(Reminder reminder) throws SchedulerException {
        log.debug("Scheduling all reminders {}", reminder);
        LocalDate alertDate = reminder.getAlertDate();
        Date from = Date.from(alertDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (reminder.getAlertTime() != null) {
            LocalTime alertTime = reminder.getAlertTime().withZoneSameInstant(LONDON_TIMEZONE).toLocalTime();
            ZonedDateTime zonedDateTime = LocalDateTime.of(alertDate, alertTime).atZone(ZoneId.systemDefault());
            from = Date.from(zonedDateTime.toInstant());
        }

        JobDetail job = newJob(ReminderJob.class).withIdentity("Reminder_J_Alert_1_" + reminder.getId()).build();
        job.getJobDataMap().put("reminderId", reminder.getId());
        Trigger trigger =
            newTrigger().withIdentity("Reminder_T_Alert_1_" + reminder.getId()).startAt(from).build();

        scheduler.scheduleJob(job, trigger);

        if (reminder.getSecondAlertDate() != null) {
            from = Date.from(reminder.getSecondAlertDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (reminder.getSecondAlertTime() != null) {
                LocalTime alertTime = reminder.getSecondAlertTime().withZoneSameInstant(LONDON_TIMEZONE).toLocalTime();
                ZonedDateTime zonedDateTime = LocalDateTime.of(alertDate, alertTime).atZone(ZoneId.systemDefault());
                from = Date.from(zonedDateTime.toInstant());
            }

            job = newJob(ReminderJob.class).withIdentity("Reminder_J_Alert_2_" + reminder.getId()).build();
            job.getJobDataMap().put("reminderId", reminder.getId());
            trigger =
                newTrigger().withIdentity("Reminder_T_Alert_2_" + reminder.getId()).startAt(from).build();

            scheduler.scheduleJob(job, trigger);
        }


    }

    public void scheduleBookingReminder(Booking booking) throws SchedulerException {

        Date from = Date.from(booking.getBookingDate().minusDays(applicationProperties.getAlertInDays()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        log.debug("Scheduling booking reminder for id {} which due on {}", booking.getId(), from);
        JobDetail job = newJob(BookingJob.class).withIdentity("Booking_J_" + booking.getId()).build();
        job.getJobDataMap().put("bookingId", booking.getId());
        Trigger trigger =
            newTrigger().withIdentity("Booking_T_" + booking.getId()).startAt(from).build();

        scheduler.scheduleJob(job, trigger);

    }

    public void sendBookingConfirmationMessage(Booking booking) {
        Customer customer = booking.getCustomer();
        StringBuilder builder = new StringBuilder();

        builder.append("Thank you ");
        builder.append(customer.getFirstName());
        builder.append(" for booking the following jobs:");
        builder.append("\n");
        for (Job job : booking.getJobs()) {
            builder.append("-");
            builder.append(job.getDescription());
            builder.append("(\u00A3" + job.getPrice() + ")");
            builder.append("\n");
        }
        builder.append("with ");
        builder.append(booking.getGarage().getBusinessName());
        builder.append("\nLet us know if you need further info by contacting us via email on ");
        builder.append(booking.getGarage().getBusinessEmail());
        builder.append(" or via phone on ");
        builder.append(booking.getGarage().getPhoneNumber());


        String areaCode = "+44";
        String to = areaCode + customer.getPhoneNumber().substring(1);

        smsService.sendSms(to, builder.toString());
    }

    public void sendCustomerMessage(Customer customer, Garage garage) {
        StringBuilder builder = new StringBuilder();

        builder.append("Thank you ");
        builder.append(customer.getFirstName());
        builder.append(" for registering with \n");
        builder.append(garage.getBusinessName());
        builder.append("\nLet us know if you need further info by contacting us via email on ");
        builder.append(garage.getBusinessEmail());
        builder.append(" or via phone on ");
        builder.append(garage.getPhoneNumber());


        String areaCode = "+44";
        String to = areaCode + customer.getPhoneNumber().substring(1);

        smsService.sendSms(to, builder.toString());
    }


    public void sendReminderMessage(Reminder reminder) throws ParseException {
        StringBuilder builder = new StringBuilder();

        builder.append("Hi ");
        builder.append(reminder.getCustomer().getFirstName());
        builder.append("\nYour vehicle registration ");
        builder.append(reminder.getVehicle().getRegistration());
        builder.append(" is due for ");
        builder.append(reminder.getDescription());
        builder.append(" on ");
        builder.append(reminder.getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        builder.append("with ");
        builder.append(reminder.getGarage().getBusinessName());
        builder.append("\nLet us know if you need further info by contacting us via email on ");
        builder.append(reminder.getGarage().getBusinessEmail());
        builder.append(" or via phone on ");
        builder.append(reminder.getGarage().getPhoneNumber());

        String areaCode = "+44";
        String to = areaCode + reminder.getCustomer().getPhoneNumber().substring(1);

        if (reminder.isSmsEnabled())
            smsService.sendSms(to, builder.toString());
        if (reminder.isEmailEnabled())
            mailService.sendReminderMail(reminder);

    }

    public void updateMOTReminder(Vehicle vehicle, Customer c, Garage garage) {

        LocalDate motExpiryDate = vehicle.getMotExpiryDate();
        if (motExpiryDate != null) {
            Reminder reminder = new Reminder();
            reminder.setVehicle(vehicle);
            reminder.setCustomer(c);
            reminder.setGarage(garage);
            reminder.setDescription("MOT Due");


            reminder.setEventDate(motExpiryDate);
            LocalDate now = LocalDate.now();

            LocalDate noticePeriod = motExpiryDate.minusMonths(1);
            LocalTime alertTime = LocalTime.of(8, 0);
            LocalDate alertDate = noticePeriod;
            LocalDate tomorrowsDate = now.plusDays(1);
            if (tomorrowsDate.isBefore(motExpiryDate)) {
                if (tomorrowsDate.isAfter(noticePeriod)) {
                    alertDate = tomorrowsDate;
                }
                reminder.setAlertDate(alertDate);
                ZonedDateTime alertTimeZonedDateTime = LocalDateTime.of(alertDate, alertTime).atZone(ZoneId.systemDefault());
                reminder.setAlertTime(alertTimeZonedDateTime);
            } else if (tomorrowsDate.isEqual(motExpiryDate) || tomorrowsDate.isAfter(motExpiryDate)) {
                alertDate = now;
                alertTime = LocalTime.now().plusSeconds(30);
                reminder.setEventDate(alertDate);
                reminder.setAlertDate(alertDate);
                ZonedDateTime alertTimeZonedDateTime = LocalDateTime.of(alertDate, alertTime).atZone(ZoneId.systemDefault());
                reminder.setAlertTime(alertTimeZonedDateTime);
            }


            reminder.setEnabled(true);
            reminder.setSmsEnabled(true);
            reminder.setEmailEnabled(true);
            Reminder result = reminderRepository.save(reminder);
            try {
                scheduleReminder(result);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    public void sendBookingReminderMessage(Booking booking) throws ParseException {
        StringBuilder builder = new StringBuilder();
        builder.append("Hi ");
        builder.append(booking.getCustomer().getFirstName());
        builder.append(" your booking is due on ");
        builder.append(booking.getBookingDate().format(DATE_FORMATTER));
        builder.append("with ");
        builder.append(booking.getGarage().getBusinessName());
        builder.append("\nLet us know if you need further info by contacting us via email on ");
        builder.append(booking.getGarage().getBusinessEmail());
        builder.append(" or via phone on ");
        builder.append(booking.getGarage().getPhoneNumber());

        String areaCode = "+44";
        String to = areaCode + booking.getCustomer().getPhoneNumber().substring(1);

        smsService.sendSms(to, builder.toString());


    }


    class BookingReminderTask implements Runnable {

        private final Booking booking;

        public BookingReminderTask(Booking booking) {

            this.booking = booking;
        }

        @Override
        public void run() {

            log.info("Schedule booking reminder for booking id : {}", booking.getId());

            try {
                sendBookingReminderMessage(booking);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class ReminderTask implements Runnable {

        private final Reminder reminder;

        public ReminderTask(Reminder reminder) {

            this.reminder = reminder;
        }

        @Override
        public void run() {
            log.info(" Reminder Task for reminder {} on thread {}", reminder, Thread.currentThread().getName());

            try {
                sendReminderMessage(reminder);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
