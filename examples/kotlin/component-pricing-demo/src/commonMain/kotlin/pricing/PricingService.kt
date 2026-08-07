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

data class Quote(
    val sku: String,
    val subtotalMinor: Long,
    val discountMinor: Long,
    val taxMinor: Long,
    val totalMinor: Long,
    val currency: String,
    val summary: String,
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
            totalMinor = totalMinor,
            currency = request.currency,
            summary = "${request.sku} x $quantity = ${request.currency} $totalMinor minor units",
        )
    }
}
