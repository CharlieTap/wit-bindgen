@file:OptIn(ExperimentalUnsignedTypes::class)

package pricing

import kotlin.test.Test
import kotlin.test.assertContentEquals
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
        assertEquals(
            listOf(
                LineItem(description = "Discount", amountMinor = -1_687),
                LineItem(description = "Tax", amountMinor = 7_162),
            ),
            quote.adjustments,
        )
        assertEquals(42_975, quote.totalMinor)
        assertEquals("APEX-PRO x 30 = GBP 42975 minor units", quote.summary)
        assertContentEquals(booleanArrayOf(true, true, true), quote.modelData.eligibilityFlags)
        assertContentEquals(ubyteArrayOf(1u, 127u, 255u), quote.modelData.featureBytes)
        assertContentEquals(byteArrayOf(-128, 0, 127), quote.modelData.signedFeatureBytes)
        assertContentEquals(ushortArrayOf(1u, 32_768u, 65_535u), quote.modelData.categoryCodes)
        assertContentEquals(shortArrayOf(-1_000, 0, 1_000), quote.modelData.seasonalAdjustments)
        assertContentEquals(uintArrayOf(0u, 30u, UInt.MAX_VALUE), quote.modelData.observationCounts)
        assertContentEquals(intArrayOf(-30, 0, 30), quote.modelData.demandAdjustments)
        assertContentEquals(ulongArrayOf(0uL, 42_975uL, ULong.MAX_VALUE), quote.modelData.revenueBuckets)
        assertContentEquals(longArrayOf(-42_975, 0, 42_975), quote.modelData.balanceAdjustments)
        assertContentEquals(floatArrayOf(0.25f, 0.5f, 0.99f), quote.modelData.confidenceScores)
        assertContentEquals(doubleArrayOf(-1.0, 0.0, 1.0), quote.modelData.calibrationValues)
        assertContentEquals(intArrayOf('G'.code, 'B'.code, 'P'.code, 0x1F4B7), quote.modelData.currencySymbols)
    }
}
