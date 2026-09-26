# Kotlin component pricing playground

This project compiles an ordinary Kotlin `PricingService` implementation to a
WebAssembly component with Kotlin 2.5.0-Beta1. The Kotlin interface, records, enums, and
implementation remain the application-owned source of truth. The WIT contract
is handwritten to keep the example focused on generated component bindings.

The build uses the `wit-bindgen` checkout containing this example. The Kotlin
backend's `--with` option maps WIT types onto the application-owned
declarations. Its `--export` option maps the exported interface to
`PricingComponent.service`, so the canonical ABI wrapper calls the existing
implementation without a generated interface or forwarding adapter.

The Kotlin bindings and embedded component metadata both select the canonical
ABI's UTF-16 string encoding, allowing Kotlin strings to be copied as their
native UTF-16 code units without UTF-8 transcoding.

The demo also selects `--primitive-lists arrays`. Its `pricing-model-data`
record covers every WIT primitive list representation and maps them to Kotlin's
unboxed primitive arrays.

The relevant layers are:

- `src/commonMain`: the Kotlin-owned interface, domain types, implementation, and component root.
- `generated/wit-bindgen`: checked-in Kotlin backend output from the local generator.

Install the host tools with Homebrew:

```shell
brew install openjdk@21 rust wasm-tools wasmtime
```

Build, validate, and invoke the pricing component:

```shell
make build
make invoke
```

## Files to inspect

- `wit/pricing.wit`: the handwritten pricing contract.
- `src/commonMain/kotlin/pricing/PricingComponent.kt`: the component root selected by `--export`.
- `generated/wit-bindgen/InternalPricingComponent.kt`: the generated canonical ABI export.
- `build/pricing-service.wit`: WIT extracted from the completed pricing component.
- `build/pricing-service.component.wasm`: the validated pricing component.
