@file:OptIn(ExperimentalUnsignedTypes::class)

package pricing

interface PricingService {
    fun quote(request: QuoteRequest): Quote
}

enum class CustomerTier {
    STANDARD,
    PREFERRED,
    ENTERPRISE,
}

enum class TaxRegion {
    EXEMPT,
    UK,
    EU,
    US,
}

data class QuoteRequest(
    val sku: String,
    val quantity: Int,
    val unitPriceMinor: Long,
    val customerTier: CustomerTier,
    val taxRegion: TaxRegion,
    val currency: String,
)

data class LineItem(
    val description: String,
    val amountMinor: Long,
)

data class PricingModelData(
    val eligibilityFlags: BooleanArray,
    val featureBytes: UByteArray,
    val signedFeatureBytes: ByteArray,
    val categoryCodes: UShortArray,
    val seasonalAdjustments: ShortArray,
    val observationCounts: UIntArray,
    val demandAdjustments: IntArray,
    val revenueBuckets: ULongArray,
    val balanceAdjustments: LongArray,
    val confidenceScores: FloatArray,
    val calibrationValues: DoubleArray,
    val currencySymbols: IntArray,
)

data class Quote(
    val sku: String,
    val subtotalMinor: Long,
    val discountMinor: Long,
    val taxMinor: Long,
    val adjustments: List<LineItem>,
    val totalMinor: Long,
    val currency: String,
    val summary: String,
    val modelData: PricingModelData,
)

class StandardPricingService : PricingService {
    override fun quote(request: QuoteRequest): Quote {
        val quantity = request.quantity.coerceAtLeast(0)
        val unitPriceMinor = request.unitPriceMinor.coerceAtLeast(0)
        val subtotalMinor = unitPriceMinor * quantity

        val volumeDiscountBasisPoints = when {
            quantity >= 100 -> 500
            quantity >= 25 -> 250
            quantity >= 10 -> 100
            else -> 0
        }
        val tierDiscountBasisPoints = when (request.customerTier) {
            CustomerTier.STANDARD -> 0
            CustomerTier.PREFERRED -> 200
            CustomerTier.ENTERPRISE -> 500
        }
        val discountBasisPoints =
            (volumeDiscountBasisPoints + tierDiscountBasisPoints).coerceAtMost(2_500)
        val discountMinor = subtotalMinor * discountBasisPoints / 10_000
        val discountedSubtotalMinor = subtotalMinor - discountMinor

        val taxBasisPoints = when (request.taxRegion) {
            TaxRegion.EXEMPT -> 0
            TaxRegion.UK -> 2_000
            TaxRegion.EU -> 2_100
            TaxRegion.US -> 825
        }
        val taxMinor = discountedSubtotalMinor * taxBasisPoints / 10_000
        val totalMinor = discountedSubtotalMinor + taxMinor

        return Quote(
            sku = request.sku,
            subtotalMinor = subtotalMinor,
            discountMinor = discountMinor,
            taxMinor = taxMinor,
            adjustments = listOf(
                LineItem(description = "Discount", amountMinor = -discountMinor),
                LineItem(description = "Tax", amountMinor = taxMinor),
            ),
            totalMinor = totalMinor,
            currency = request.currency,
            summary = "${request.sku} x $quantity = ${request.currency} $totalMinor minor units",
            modelData = PricingModelData(
                eligibilityFlags = booleanArrayOf(true, quantity >= 10, discountMinor > 0),
                featureBytes = ubyteArrayOf(1u, 127u, 255u),
                signedFeatureBytes = byteArrayOf(-128, 0, 127),
                categoryCodes = ushortArrayOf(1u, 32_768u, 65_535u),
                seasonalAdjustments = shortArrayOf(-1_000, 0, 1_000),
                observationCounts = uintArrayOf(0u, quantity.toUInt(), UInt.MAX_VALUE),
                demandAdjustments = intArrayOf(-quantity, 0, quantity),
                revenueBuckets = ulongArrayOf(0uL, totalMinor.toULong(), ULong.MAX_VALUE),
                balanceAdjustments = longArrayOf(-totalMinor, 0, totalMinor),
                confidenceScores = floatArrayOf(0.25f, 0.5f, 0.99f),
                calibrationValues = doubleArrayOf(-1.0, 0.0, 1.0),
                currencySymbols = intArrayOf('G'.code, 'B'.code, 'P'.code, 0x1F4B7),
            ),
        )
    }
}
