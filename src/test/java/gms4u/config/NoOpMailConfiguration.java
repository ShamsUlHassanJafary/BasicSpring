package gms4u.config;

import gms4u.service.MailService;
import gms4u.service.SMSService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Configuration
public class NoOpMailConfiguration {
    private final MailService mockMailService;

    private final SMSService mockSmsService;

    public NoOpMailConfiguration() {
        mockMailService = mock(MailService.class);
        doNothing().when(mockMailService).sendActivationEmail(any());
        mockSmsService = mock(SMSService.class);;
        doNothing().when(mockSmsService).sendSms(any(), any());
    }

    @Bean
    public MailService mailService() {
        return mockMailService;
    }

    @Bean
    public SMSService smsService() {
        return mockSmsService;
    }
}
