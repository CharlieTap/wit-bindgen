# Kotlin component pricing playground

This project compiles an ordinary Kotlin `PricingService` implementation to a
WebAssembly component with Kotlin 2.4. The Kotlin interface, records, enums, and
implementation remain the application-owned source of truth. The WIT contract
is handwritten to keep the example focused on generated component bindings.

The build uses the `wit-bindgen` checkout containing this example. The WIT world
imports its data types and the Kotlin backend's `--with` option maps that
interface onto the existing application-owned `pricing` package. The generated
pricing export therefore uses `QuoteRequest` and an explicit `price-quote` to
`Quote` type remapping rather than generating duplicate domain types.

The Kotlin bindings and embedded component metadata both select the canonical
ABI's UTF-16 string encoding, allowing Kotlin strings to be copied as their
native UTF-16 code units without UTF-8 transcoding.

The demo also selects `--primitive-lists arrays`. Its `pricing-model-data`
record covers every WIT primitive list representation and maps them to Kotlin's
unboxed primitive arrays.

The relevant layers are:

- `src/commonMain`: the Kotlin-owned interface, domain types, and implementation.
- `src/wasmWasiMain`: the implementation of the generated pricing export.
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
- `generated/wit-bindgen/PricingComponent.kt`: the generated export interface using application types.
- `src/wasmWasiMain/kotlin/bindings/PricingImpl.kt`: the implementation that delegates to the service.
- `build/pricing-service.wit`: WIT extracted from the completed pricing component.
- `build/pricing-service.component.wasm`: the validated pricing component.
