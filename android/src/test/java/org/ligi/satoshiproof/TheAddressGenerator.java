package org.ligi.satoshiproof;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.satoshiproof.util.AddressGenerator.dataToAddressString;

public class TheAddressGenerator {

    @Test
    public void shouldGenerateCorrectAddresses() {
        assertThat(dataToAddressString("probe".getBytes())).isEqualTo("1KXgQMLN5ceej3ViannhJudCjJsFWrWXvs");

        assertThat(dataToAddressString("foo".getBytes())).isEqualTo("1MaybZp8GRkAHmpAyWkSQEwAnohFxPBoGY");
    }
}