package gms4u.service;

import gms4u.domain.*;
import gms4u.service.dto.FileDto;
import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    public static final String EMAIL_DOES_NOT_EXIST_FOR_USER = "Email doesn't exist for user '{}'";
    private static final String USER = "user";
    private static final String VEHICLE = "vehicle";
    private static final String BASE_URL = "baseUrl";
    private final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
                       MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isHtml, FileDto fileDto) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            true, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            File file = new File(fileDto.getFilename());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileDto.getData());
            message.addAttachment(fileDto.getFilename(), file);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException | IOException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(Quote quote, String templateName, String titleKey) {
        Booking booking = quote.getBooking();
        if (booking.getCustomer().getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, booking.getCustomer().getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("quote", quote);
        context.setVariable("booking", booking);
        context.setVariable("customer", booking.getCustomer());
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(booking.getCustomer().getEmail(), subject, content, false, true);
    }


    @Async
    public void sendEmailFromTemplate(Quote quote, String templateName, String titleKey, FileDto fileDto) {
        Booking booking = quote.getBooking();
        final Customer customer = booking.getCustomer();
        if (customer.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, customer.getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("quote", quote);
        context.setVariable("booking", booking);
        context.setVariable("customer", customer);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(customer.getEmail(), subject, content, true, fileDto);
    }


    @Async
    public void sendEmailFromTemplate(Invoice invoice, String templateName, String titleKey, FileDto fileDto) {
        Booking booking = invoice.getBooking();
        final Customer customer = booking.getCustomer();
        if (customer.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, customer.getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("invoice", invoice);
        context.setVariable("booking", booking);
        context.setVariable("customer", customer);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(customer.getEmail(), subject, content, true, fileDto);
    }

    @Async
    public void sendEmailFromTemplate(Customer customer, Vehicle vehicle, Garage garage, String templateName, String titleKey, FileDto fileDto) {
        if (customer.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, customer.getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("customer", customer);
        context.setVariable("vehicle", vehicle);
        context.setVariable("garage", garage);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        Object[] params = {vehicle.getRegistration()};
        String subject = messageSource.getMessage(titleKey, params, locale);
        sendEmail(customer.getEmail(), subject, content, true, fileDto);
    }

    @Async
    public void sendEmailFromTemplate(Customer customer, Vehicle vehicle, Garage garage, String templateName, String titleKey) {
        if (customer.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, customer.getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("customer", customer);
        context.setVariable("vehicle", vehicle);
        context.setVariable("garage", garage);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        Object[] params = {vehicle.getRegistration()};
        String subject = messageSource.getMessage(titleKey, params, locale);
        sendEmail(customer.getEmail(), subject, content, false, false);
    }

    @Async
    public void sendEmailFromTemplate(Reminder reminder, String templateName, String titleKey) {
        if (reminder.getCustomer().getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, reminder.getCustomer().getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("customer", reminder.getCustomer());
        context.setVariable("vehicle", reminder.getVehicle());
        context.setVariable("reminder", reminder);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(reminder.getCustomer().getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailFromTemplate(Booking booking, String templateName, String titleKey) {
        if (booking.getCustomer().getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, booking.getCustomer().getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("booking", booking);
        context.setVariable("customer", booking.getCustomer());
        context.setVariable("vehicle", booking.getVehicle());
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(booking.getCustomer().getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, user.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailFromTemplate(Customer customer, Garage garage, String templateName, String titleKey) {
        if (customer.getEmail() == null) {
            log.debug(EMAIL_DOES_NOT_EXIST_FOR_USER, customer.getEmail());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("customer", customer);
        context.setVariable("garage", garage);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, new Object[]{garage.getBusinessName()}, locale);
        sendEmail(customer.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendCustomerRegistrationEmail(Customer customer, Garage garage) {
        log.debug("Sending customer registration email to '{}'", customer.getEmail());
        sendEmailFromTemplate(customer, garage, "mail/customerRegistrationEmail", "email.customer.registration.title");
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    @Async
    public void sendMotDueReminderMail(Vehicle vehicle, Customer customer, Garage garage) {
        if (customer != null) {
            log.debug("Sending MOT due reminder email to '{}'", customer.getEmail());
            sendEmailFromTemplate(customer, vehicle, garage, "mail/motDueReminderEmail", "email.motDueReminder.title");
        }
    }

    @Async
    public void sendReminderMail(Reminder reminder) {
        if (reminder != null) {
            log.debug("Sending Reminder email.");
            sendEmailFromTemplate(reminder, "mail/reminderEmail", "email.reminder.title");
        }
    }


    @Async
    public void sendBookingConfirmationMail(Booking booking) {
        if (booking.getCustomer() != null) {
            log.debug("Sending MOT due reminder email to '{}'", booking.getCustomer().getEmail());
            sendEmailFromTemplate(booking, "mail/confirmBookingEmail", "email.booking.title");
        }
    }

    @Async
    public void sendQuotationMail(Quote quote, FileDto fileDto) {
        Booking booking = quote.getBooking();
        if (booking.getCustomer() != null) {
            log.debug("Sending Quotation email to '{}'", booking.getCustomer().getEmail());
            sendEmailFromTemplate(quote, "mail/quotationEmail", "email.quotation.title", fileDto);
        }
    }

    @Async
    public void sendInvoiceMail(Invoice invoice, FileDto fileDto) {
        Booking booking = invoice.getBooking();
        if (booking.getCustomer() != null) {
            log.debug("Sending Invoice email to '{}'", booking.getCustomer().getEmail());
            sendEmailFromTemplate(invoice, "mail/invoiceEmail", "email.invoice.title", fileDto);
        }
    }

    @Async
    public void sendServiceHistoryReportMail(Customer customer, Vehicle vehicle, Garage garage, FileDto fileDto) {

        log.debug("Sending Service History Report email to '{}'", customer.getEmail());
        sendEmailFromTemplate(customer, vehicle, garage, "mail/serviceHistoryReportEmail", "email.serviceHistory.title", fileDto);

    }

}
