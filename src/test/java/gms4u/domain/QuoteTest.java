package gms4u.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import gms4u.web.rest.TestUtil;

public class QuoteTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quote.class);
        Quote quote1 = new Quote();
        quote1.setId(1L);
        Quote quote2 = new Quote();
        quote2.setId(quote1.getId());
        assertThat(quote1).isEqualTo(quote2);
        quote2.setId(2L);
        assertThat(quote1).isNotEqualTo(quote2);
        quote1.setId(null);
        assertThat(quote1).isNotEqualTo(quote2);
    }
}
