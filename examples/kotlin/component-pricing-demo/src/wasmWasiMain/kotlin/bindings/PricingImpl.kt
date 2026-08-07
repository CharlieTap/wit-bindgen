package bindings

import pricing.PricingService
import pricing.Quote
import pricing.QuoteRequest
import pricing.StandardPricingService

object PricingImpl : Pricing {
    private val service: PricingService = StandardPricingService()

    override fun quote(request: QuoteRequest): Quote = service.quote(request)
}
