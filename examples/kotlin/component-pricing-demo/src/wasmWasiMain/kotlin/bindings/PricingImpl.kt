package bindings

import pricing.PricingService
import pricing.StandardPricingService
import pricing.CustomerTier as DomainCustomerTier
import pricing.QuoteRequest as DomainQuoteRequest
import pricing.TaxRegion as DomainTaxRegion

object PricingImpl : Pricing {
    private val service: PricingService = StandardPricingService()

    override fun quote(request: Pricing.QuoteRequest): Pricing.PriceQuote {
        val quote = service.quote(
            DomainQuoteRequest(
                sku = request.sku,
                quantity = request.quantity,
                unitPriceMinor = request.unitPriceMinor,
                customerTier = when (request.customerTier) {
                    Pricing.CustomerTier.STANDARD -> DomainCustomerTier.STANDARD
                    Pricing.CustomerTier.PREFERRED -> DomainCustomerTier.PREFERRED
                    Pricing.CustomerTier.ENTERPRISE -> DomainCustomerTier.ENTERPRISE
                },
                taxRegion = when (request.taxRegion) {
                    Pricing.TaxRegion.EXEMPT -> DomainTaxRegion.EXEMPT
                    Pricing.TaxRegion.UK -> DomainTaxRegion.UK
                    Pricing.TaxRegion.EU -> DomainTaxRegion.EU
                    Pricing.TaxRegion.US -> DomainTaxRegion.US
                },
                currency = request.currency,
            ),
        )

        return Pricing.PriceQuote(
            sku = quote.sku,
            subtotalMinor = quote.subtotalMinor,
            discountMinor = quote.discountMinor,
            taxMinor = quote.taxMinor,
            totalMinor = quote.totalMinor,
            currency = quote.currency,
            summary = quote.summary,
        )
    }
}
