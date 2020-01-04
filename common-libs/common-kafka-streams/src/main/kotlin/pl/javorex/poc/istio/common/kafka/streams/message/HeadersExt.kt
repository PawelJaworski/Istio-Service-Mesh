package pl.javorex.poc.istio.common.kafka.streams.message

import org.apache.kafka.common.header.Headers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8


fun Headers.addString(key: String, value: String) {
    this.add(key, value.toByteArray(UTF_8))
}

fun Headers.getString(key: String): String {
    assert(headerExists(key)) {
        "Header $key not exists."
    }

    val header = this.lastHeader(key)
    return String(header!!.value())
}

fun Headers.addLong(key: String, value: Long) {
    val longAsBytes = ByteUtils.longToBytes(value)
    this.add(key, longAsBytes)
}

fun Headers.getLong(key: String): Long {
    assert(headerExists(key)) {
        "Header $key not exists."
    }
    val header = this.lastHeader(key)
    return ByteUtils.bytesToLong(header.value())
}

fun Headers.headerExists(key: String) = this.lastHeader(key) != null

private object ByteUtils {
    private val buffer: ByteBuffer = ByteBuffer.allocate(java.lang.Long.BYTES)
    fun longToBytes(x: Long): ByteArray {
        buffer.putLong(0, x)
        return buffer.array()
    }

    fun bytesToLong(bytes: ByteArray): Long {
        val buffer = ByteBuffer.wrap(bytes)
        return buffer.long
    }
}
