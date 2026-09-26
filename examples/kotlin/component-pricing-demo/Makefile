JAVA_HOME ?= /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
WIT_BINDGEN_REPO ?= ../../..
WIT_BINDGEN := $(WIT_BINDGEN_REPO)/target/release/wit-bindgen
WASMTIME_VERSION := 47.0.3
ADAPTER := tools/adapters/wasi_snapshot_preview1.reactor.wasm
GENERATED := generated/wit-bindgen
CORE_DIR := build/compileSync/wasmWasi/main/productionExecutable/optimized
CORE_WASM := $(CORE_DIR)/pricing-component-playground.wasm
EMBEDDED_WASM := $(CORE_DIR)/pricing-component-playground-embedded.wasm
COMPONENT_WASM := build/pricing-service.component.wasm

.PHONY: setup bindgen generate test compile component inspect invoke build clean

build: setup generate test component inspect

setup: bindgen $(ADAPTER)

bindgen:
	cargo build --release -p wit-bindgen-cli --manifest-path $(WIT_BINDGEN_REPO)/Cargo.toml

$(ADAPTER):
	mkdir -p tools/adapters
	curl --fail --location --output $(ADAPTER) https://github.com/bytecodealliance/wasmtime/releases/download/v$(WASMTIME_VERSION)/wasi_snapshot_preview1.reactor.wasm

generate: bindgen
	rm -rf $(GENERATED)
	mkdir -p $(GENERATED)
	$(WIT_BINDGEN) kotlin \
		--kotlin-package-name bindings \
		--generate-cabi-realloc-export false \
		--string-encoding utf16 \
		--primitive-lists arrays \
		--with 'example:pricing/pricing@1.0.0/customer-tier=pricing.CustomerTier' \
		--with 'example:pricing/pricing@1.0.0/tax-region=pricing.TaxRegion' \
		--with 'example:pricing/pricing@1.0.0/quote-request=pricing.QuoteRequest' \
		--with 'example:pricing/pricing@1.0.0/line-item=pricing.LineItem' \
		--with 'example:pricing/pricing@1.0.0/pricing-model-data=pricing.PricingModelData' \
		--with 'example:pricing/pricing@1.0.0/price-quote=pricing.Quote' \
		--export 'example:pricing/pricing@1.0.0=pricing.PricingComponent.service' \
		--out-dir $(GENERATED) \
		wit

test: generate
	JAVA_HOME=$(JAVA_HOME) ./gradlew wasmWasiNodeTest

compile: generate
	JAVA_HOME=$(JAVA_HOME) ./gradlew compileProductionExecutableKotlinWasmWasiOptimize

component: compile $(ADAPTER)
	wasm-tools component embed --encoding utf16 wit $(CORE_WASM) --output $(EMBEDDED_WASM)
	wasm-tools component new $(EMBEDDED_WASM) --adapt wasi_snapshot_preview1=$(ADAPTER) --output $(COMPONENT_WASM)

inspect: component
	wasm-tools validate $(COMPONENT_WASM)
	wasm-tools component wit $(COMPONENT_WASM) > build/pricing-service.wit
	cat build/pricing-service.wit

invoke: component
	wasmtime run --invoke 'quote({sku: "APEX-PRO", quantity: 30, unit-price-minor: 1250, customer-tier: preferred, tax-region: uk, currency: "GBP"})' $(COMPONENT_WASM)

clean:
	JAVA_HOME=$(JAVA_HOME) ./gradlew clean
	rm -rf generated
