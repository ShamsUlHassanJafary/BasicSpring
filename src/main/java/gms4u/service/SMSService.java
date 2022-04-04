package gms4u.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import gms4u.config.ApplicationProperties;
import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending sms messages.
 * <p>
 * We use the {@link Async} annotation to send sms messages asynchronously.
 */
@Service("smsService")
public class SMSService {


    private final Logger log = LoggerFactory.getLogger(SMSService.class);

    private final JHipsterProperties jHipsterProperties;

    private final ApplicationProperties applicationProperties;

    public SMSService(JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        this.jHipsterProperties = jHipsterProperties;
        this.applicationProperties = applicationProperties;
    }


    @Async
    public void sendSms(String to, String content) {
        ApplicationProperties.Twilio twilio = applicationProperties.getTwilio();
        Twilio.init(twilio.getAccountSid(), twilio.getAuthToken());

        log.debug("Sending SMS to '{}'", to);

        Message.creator(new PhoneNumber(to), new PhoneNumber(twilio.getAccountNumber()), content).create();

    }


}
