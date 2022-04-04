package gms4u.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import gms4u.web.rest.TestUtil;

public class GarageTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Garage.class);
        Garage garage1 = new Garage();
        garage1.setId(1L);
        Garage garage2 = new Garage();
        garage2.setId(garage1.getId());
        assertThat(garage1).isEqualTo(garage2);
        garage2.setId(2L);
        assertThat(garage1).isNotEqualTo(garage2);
        garage1.setId(null);
        assertThat(garage1).isNotEqualTo(garage2);
    }
}
