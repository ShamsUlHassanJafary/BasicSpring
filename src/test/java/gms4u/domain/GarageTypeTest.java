package gms4u.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import gms4u.web.rest.TestUtil;

public class GarageTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GarageType.class);
        GarageType garageType1 = new GarageType();
        garageType1.setId(1L);
        GarageType garageType2 = new GarageType();
        garageType2.setId(garageType1.getId());
        assertThat(garageType1).isEqualTo(garageType2);
        garageType2.setId(2L);
        assertThat(garageType1).isNotEqualTo(garageType2);
        garageType1.setId(null);
        assertThat(garageType1).isNotEqualTo(garageType2);
    }
}
