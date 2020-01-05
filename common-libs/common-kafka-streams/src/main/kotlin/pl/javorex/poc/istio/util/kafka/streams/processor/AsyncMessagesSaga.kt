package pl.javorex.poc.istio.util.kafka.streams.processor

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import pl.javorex.poc.istio.util.kafka.streams.JsonPojoSerde
import pl.javorex.poc.istio.util.messaging.AsyncMessagesBuilder
import pl.javorex.poc.istio.util.messaging.CurrentMessages
import pl.javorex.poc.istio.util.messaging.AsyncMessageCallback
import java.time.Duration

class AsyncMessagesSaga<M> {
    private lateinit var starts: Class<*>
    private lateinit var timeout: Duration
    private val steps = hashSetOf<SagaStep<M>>()
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

    fun step(): SagaStep<M> {
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

class SagaStep<M>(private val saga: AsyncMessagesSaga<M>) {
    private lateinit var processorName: String
    private lateinit var storeName: String
    private val async = AsyncMessagesBuilder<M>()
    private var heartBeatInterval =
            HeartBeatInterval.ofSeconds(5)
    private lateinit var _onComplete: AsyncMessageCallback<M>
    private lateinit var _sources: Array<out String>

    fun processorName() = processorName

    fun named(named: String): SagaStep<M> {
        this.processorName = "$named-processor"
        this.storeName = "$named-store"
        return this
    }

    fun requires(clazz: Class<*>): SagaStep<M> {
        async.requires(clazz)
        return this
    }

    fun heartBeat(heartBeatInterval: HeartBeatInterval): SagaStep<M> {
        this.heartBeatInterval = heartBeatInterval
        return this
    }

    fun onComplete(onComplete: AsyncMessageCallback<M>): SagaStep<M> {
        this._onComplete = onComplete
        return this
    }

    fun sources(vararg sources: String): SagaStep<M> {
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
