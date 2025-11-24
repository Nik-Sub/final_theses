package ai.cargominds.ktorutils.metrics

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.binder.MeterBinder
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

private val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

@Suppress("MagicNumber")
public fun Application.configureMetrics(extraMetrics: Set<MeterBinder> = emptySet()) {
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        distributionStatisticConfig = DistributionStatisticConfig
            .Builder()
            .percentilesHistogram(true)
            .percentiles(0.5, 0.9, 0.95, 0.99)
            .maximumExpectedValue(20.seconds.toDouble(DurationUnit.NANOSECONDS))
            .serviceLevelObjectives(
                100.seconds.toDouble(DurationUnit.NANOSECONDS),
                500.seconds.toDouble(DurationUnit.NANOSECONDS)
            )
            .build()
        meterBinders += extraMetrics + UptimeMetrics()
    }

    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
