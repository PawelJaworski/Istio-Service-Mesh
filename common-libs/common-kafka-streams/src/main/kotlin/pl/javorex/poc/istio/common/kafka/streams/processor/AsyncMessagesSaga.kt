package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import pl.javorex.poc.istio.common.kafka.streams.JsonPojoSerde
import pl.javorex.poc.istio.common.message.async.AsyncMessagesBuilder
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback
import java.time.Duration

class AsyncMessagesSaga {
    private lateinit var starts: Class<*>
    private lateinit var timeout: Duration
    private val steps = hashSetOf<SagaStep>()
    private lateinit var topic: String
    private lateinit var sink: String
    private lateinit var errTopic: String
    private lateinit var errorSink: String

    fun startsWith() = starts

    fun startsWith(starts: Class<*>) {
        this.starts = starts
    }

    fun timeout() = timeout
    fun timeout(timeout: Duration) {
        this.timeout = timeout
    }

    fun sink() = sink
    fun errSink() = errorSink
    fun errTopic() = errTopic
    fun topic(topic: String, errTopic: String) {
        this.topic = topic
        this.sink = "$topic-sink"
        this.errTopic = errTopic
        this.errorSink = "$errTopic-sink"
    }

    fun step(): SagaStep {
        val newStep = SagaStep(this)
        steps += newStep

        return newStep
    }

    fun joinInto(kStreamTopology: Topology) {
        steps.forEach {
            it.joinInto(kStreamTopology)
        }

        val processors = steps
                .map { it.processorName() }
                .toTypedArray()

        kStreamTopology.addSink(sink, topic, *processors)
        kStreamTopology.addSink(errorSink, errTopic, *processors)
    }
}

class SagaStep(private val saga: AsyncMessagesSaga) {
    private lateinit var processorName: String
    private lateinit var storeName: String
    private val async = AsyncMessagesBuilder()
    private var heartBeatInterval =
        HeartBeatInterval.ofSeconds(5)
    private lateinit var _onComplete: AsyncMessageCallback
    private lateinit var _sources: Array<out String>

    fun processorName() = processorName

    fun named(named: String): SagaStep {
        this.processorName = "$named-processor"
        this.storeName = "$named-store"
        return this
    }

    fun requires(clazz: Class<*>): SagaStep {
        async.requires(clazz)
        return this
    }

    fun heartBeat(heartBeatInterval: HeartBeatInterval): SagaStep {
        this.heartBeatInterval = heartBeatInterval
        return this
    }

    fun onComplete(onComplete: AsyncMessageCallback): SagaStep {
        this._onComplete = onComplete
        return this
    }

    fun sources(vararg sources: String): SagaStep {
        this._sources = sources
        return this
    }

    fun joinInto(kStreamTopology: Topology) {
        async.startsWith(saga.startsWith())
        async.withTimeout(saga.timeout())

        val storeBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(storeName),
                        Serdes.String(),
                    JsonPojoSerde(CurrentMessages::class.java)
                )
        kStreamTopology.addProcessor(
                processorName,
                ProcessorSupplier {
                    AsyncMessagesProcessor(
                        { async.build() },
                        heartBeatInterval,
                        storeName,
                        _onComplete,
                        saga.sink(),
                        saga.errSink(),
                        saga.errTopic()
                    )
                },
                *_sources
        )
        kStreamTopology.addStateStore(storeBuilder, processorName)
    }
}
