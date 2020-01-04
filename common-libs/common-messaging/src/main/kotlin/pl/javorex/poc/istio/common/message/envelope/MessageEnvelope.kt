package pl.javorex.poc.istio.common.message.envelope

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class MessageEnvelope<M>(
        val sourceId: String = "",
        val sourceVersion: Long = Long.MIN_VALUE,
        val timestamp: Long = Long.MIN_VALUE,
        val messageType: String = "",
        val payload: M? = null
)

fun <M>pack(sourceId: String, sourceVersion: Long, messageType: String, message: M): MessageEnvelope<M> {
    return MessageEnvelope(sourceId, sourceVersion, System.currentTimeMillis(), messageType, message)
}
