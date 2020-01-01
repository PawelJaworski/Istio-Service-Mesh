package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.ProcessorContext

fun ProcessorContext.getHeader(key: String) : String {
    val header = this.headers()
            .lastHeader(key)

    return String(header!!.value())
}

fun ProcessorContext.headerExists(key: String) = this.headers().lastHeader(key) != null
