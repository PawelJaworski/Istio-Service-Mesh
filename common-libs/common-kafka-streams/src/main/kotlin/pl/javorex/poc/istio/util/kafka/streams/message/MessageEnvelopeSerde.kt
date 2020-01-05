package pl.javorex.poc.istio.util.kafka.streams.message

import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import pl.javorex.poc.istio.util.kafka.streams.JsonPOJODeserializer
import pl.javorex.poc.istio.util.kafka.streams.JsonPojoSerde
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope

class MessageEnvelopeSerde(
    private val s: JsonPojoSerde<MessageEnvelope<*>> = JsonPojoSerde(
            MessageEnvelope::class.java
    )
) : Serde<MessageEnvelope<*>> by s

class MessageEnvelopeDeserializer(
    private val d: Deserializer<MessageEnvelope<*>> = JsonPOJODeserializer(MessageEnvelope::class.java
    )
) : Deserializer<MessageEnvelope<*>> by d
