package pl.javorex.poc.istio.util.kafka.streams

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class JsonPojoSerde<T>(tClass: Class<T>) : Serde<T> {
    private val serializer = JsonPOJOSerializer<T>()
    private val deserializer = JsonPOJODeserializer(tClass)

    override fun configure(props: MutableMap<String, *>, p1: Boolean) {}

    override fun deserializer() = deserializer

    override fun close() {}

    override fun serializer() = serializer
}

class JsonPOJOSerializer<T> : Serializer<T> {
    private val objectMapper = ObjectMapper()
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .writerWithDefaultPrettyPrinter()

    override fun configure(props: Map<String, *>, isKey: Boolean) {}

    override fun serialize(topic: String, data: T?): ByteArray? {
        if (data == null)
            return null

        try {
            return objectMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error serializing JSON message", e)
        }

    }

    override fun close() {}
}

class JsonPOJODeserializer<T>(private val tClass: Class<T>) : Deserializer<T> {
    private val objectMapper = ObjectMapper()
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

    override fun configure(props: Map<String, *>, isKey: Boolean) {}

    override fun deserialize(topic: String, bytes: ByteArray?): T? {
        if (bytes == null)
            return null

        val data: T
        try {
            data = objectMapper.readValue(bytes, tClass)
        } catch (e: Exception) {
            throw SerializationException(e)
        }

        return data
    }

    override fun close() {}

}
