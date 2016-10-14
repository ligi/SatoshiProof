package org.ligi.satoshiproof

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.satoshiproof.util.AddressGenerator.dataToAddressString

class TheAddressGenerator {

    @Test
    fun shouldGenerateCorrectAddresses() {
        assertThat(dataToAddressString("probe".toByteArray())).isEqualTo("1KXgQMLN5ceej3ViannhJudCjJsFWrWXvs")

        assertThat(dataToAddressString("foo".toByteArray())).isEqualTo("1MaybZp8GRkAHmpAyWkSQEwAnohFxPBoGY")
    }
}