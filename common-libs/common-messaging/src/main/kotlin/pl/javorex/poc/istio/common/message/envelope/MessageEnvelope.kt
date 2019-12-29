package pl.javorex.poc.istio.common.message.envelope

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class MessageEnvelope(
    val sourceId: String = "",
    val sourceVersion: Long = Long.MIN_VALUE,
    val timestamp: Long = Long.MIN_VALUE,
    val messageType: String = "",
    val payload: JsonNode = NullNode.instance
) {
    fun isTypeOf(clazz: Class<*>) = clazz.simpleName == messageType
    fun <T>unpack(clazz: Class<T>): T = newObjectMapper().treeToValue(payload, clazz)
    fun isVersionOf(sourceId: String, sourceVersion: Long) =
        this.sourceId == sourceId && this.sourceVersion == sourceVersion

    fun asString() = "{$sourceId, $sourceVersion, $payload,${LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone
            .getDefault().toZoneId())}}"
}

fun pack(sourceId: String, sourceVersion: Long, message: Any): MessageEnvelope {
    val messageType = message::class.java.simpleName
    val payload = newObjectMapper().convertValue(message, JsonNode::class.java)

    return MessageEnvelope(sourceId, sourceVersion, System.currentTimeMillis(), messageType, payload)
}

private fun newObjectMapper(): ObjectMapper = ObjectMapper()
        .registerModule(ParameterNamesModule())
        .registerModule(Jdk8Module())
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
