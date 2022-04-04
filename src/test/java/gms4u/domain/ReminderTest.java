package gms4u.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import gms4u.web.rest.TestUtil;

public class ReminderTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reminder.class);
        Reminder reminder1 = new Reminder();
        reminder1.setId(1L);
        Reminder reminder2 = new Reminder();
        reminder2.setId(reminder1.getId());
        assertThat(reminder1).isEqualTo(reminder2);
        reminder2.setId(2L);
        assertThat(reminder1).isNotEqualTo(reminder2);
        reminder1.setId(null);
        assertThat(reminder1).isNotEqualTo(reminder2);
    }
}
