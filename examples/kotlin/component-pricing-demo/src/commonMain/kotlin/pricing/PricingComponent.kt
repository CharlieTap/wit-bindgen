package pricing

import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph
internal interface PricingGraph {
    val service: PricingService

    @Binds
    val StandardPricingService.bind: PricingService

    @Binds
    val StandardDiscountPolicy.bind: DiscountPolicy

    @Binds
    val StandardTaxPolicy.bind: TaxPolicy
}

// This would be generated in the compiler
internal object PricingComponent {
    val service: PricingService = createGraph<PricingGraph>().service
}
