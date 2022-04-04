package gms4u.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import gms4u.web.rest.TestUtil;

public class GarageAdminTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GarageAdmin.class);
        GarageAdmin garageAdmin1 = new GarageAdmin();
        garageAdmin1.setId(1L);
        GarageAdmin garageAdmin2 = new GarageAdmin();
        garageAdmin2.setId(garageAdmin1.getId());
        assertThat(garageAdmin1).isEqualTo(garageAdmin2);
        garageAdmin2.setId(2L);
        assertThat(garageAdmin1).isNotEqualTo(garageAdmin2);
        garageAdmin1.setId(null);
        assertThat(garageAdmin1).isNotEqualTo(garageAdmin2);
    }
}
