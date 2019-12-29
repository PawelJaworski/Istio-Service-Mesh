package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.ProcessorContext

fun ProcessorContext.getHeader(key: String) : String? {
    val header = this.headers()
            .lastHeader(key)

    if (header != null) {
        return String(header.value())
    }

    return null
}
