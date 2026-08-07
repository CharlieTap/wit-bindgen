package pricing

import kotlin.test.Test
import kotlin.test.assertEquals

class StandardPricingServiceTest {
    @Test
    fun calculatesVolumeTierDiscountAndTax() {
        val quote = StandardPricingService().quote(
            QuoteRequest(
                sku = "APEX-PRO",
                quantity = 30,
                unitPriceMinor = 1_250,
                customerTier = CustomerTier.PREFERRED,
                taxRegion = TaxRegion.UK,
                currency = "GBP",
            ),
        )

        assertEquals(37_500, quote.subtotalMinor)
        assertEquals(1_687, quote.discountMinor)
        assertEquals(7_162, quote.taxMinor)
        assertEquals(42_975, quote.totalMinor)
        assertEquals("APEX-PRO x 30 = GBP 42975 minor units", quote.summary)
    }
}
