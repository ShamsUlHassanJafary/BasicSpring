package gms4u.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Gms 4 U.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

//    public static class QuartzDataSource {
//        private String jdbcUrl;
//        private String username;
//        private String password;
//
//        public String getJdbcUrl() {
//            return jdbcUrl;
//        }
//
//        public void setJdbcUrl(String jdbcUrl) {
//            this.jdbcUrl = jdbcUrl;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//        public void setUsername(String username) {
//            this.username = username;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//    }
//
//    private final QuartzDataSource quartzDataSource = new QuartzDataSource();
//
//    public QuartzDataSource getQuartzDataSource() {
//        return quartzDataSource;
//    }

    private final Twilio twilio = new Twilio();
    private final Dvla dvla = new Dvla();
    private int alertInDays;

    public Twilio getTwilio() {
        return twilio;
    }

    public int getAlertInDays() {
        return alertInDays;
    }

    public void setAlertInDays(int alertInDays) {
        this.alertInDays = alertInDays;
    }

    public Dvla getDvla() {
        return dvla;
    }

    public static class Dvla {

        private String url;
        private String key;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Twilio {
        private String accountSid;
        private String authToken;
        private String accountNumber;

        public Twilio() {
        }

        @Override
        public String toString() {
            return "Twilio{" +
                "accountSid='" + accountSid + '\'' +
                ", authToken='" + authToken + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
        }

        public String getAccountSid() {
            return accountSid;
        }

        public void setAccountSid(String accountSid) {
            this.accountSid = accountSid;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }
}
