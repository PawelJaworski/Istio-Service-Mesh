package pl.javorex.poc.istio.common.kafka.streams.processor

import groovy.transform.CompileStatic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.jetbrains.annotations.NotNull
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback

import java.time.Duration

class ExampleTopology(
        topicA: String,
        errorTopicA: String,
        topicB: String,
        errorTopicB: String,
        topicC: String,
        errorTopicC: String
) {
    private val props = new Properties()

    final String topicA
    final String errorTopicA
    final String topicB
    final String errorTopicB
    final String topicC
    final String errorTopicC
    final Topology topology

    boolean isBCompleted
    boolean isCCompleted

    static {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "Create-Loan-Stream"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "0.0.0.0:0001"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde.class
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde.class
        props[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        props[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000
    }

    ExampleTopology(String topicA, String errorTopicA, String topicB, String errorTopicB, String topicC,
                      String errorTopicC) {
        this.topicA = topicA
        this.errorTopicA = errorTopicA
        this.topicB = topicB
        this.errorTopicB = errorTopicB
        this.topicC = topicC
        this.errorTopicC = errorTopicC

        topology = create()
    }

    private Topology create() {
        Topology topology = createTopology()

        AsyncMessagesSaga saga = new AsyncMessagesSaga()
        saga.timeout(Duration.ofSeconds(5))
        saga.topic(topicA, errorTopicA)
        saga.startsWith(Event_A_1.class)

        saga.step()
                .named("fetch_B-1_and_B-2")
                .heartBeat(HeartBeatInterval.ofSeconds(2))
                .sources(sourceFrom(topicA), sourceFrom(topicB), sourceFrom(errorTopicB))
                .requires(Event_B_1)
                .requires(Event_B_2)
                .onComplete(new CompleteB())
        saga.step()
                .named("fetch_C-1")
                .heartBeat(HeartBeatInterval.ofSeconds(2))
                .sources(sourceFrom(topicA), sourceFrom(topicC), sourceFrom(errorTopicC))
                .requires(Event_C_1)
                .expectsError(Error_C_1)
                .onComplete(new CompleteC())

        saga.joinInto(topology)

       return topology
    }

    private Topology createTopology() {
        new StreamsBuilder().build()
                .addSource(sourceFrom(topicA), topicA)
                .addSource(sourceFrom(topicB), topicB)
                .addSource(sourceFrom(errorTopicB), errorTopicB)
                .addSource(sourceFrom(topicC), topicC)
                .addSource(sourceFrom(errorTopicC), errorTopicC)
    }

    private String sourceFrom(String topic) {
        "$topic-source"
    }

@CompileStatic
    class CompleteB implements AsyncMessageCallback {
        @Override
        void onComplete(
                @NotNull String sourceId,
                long sourceVersion,
                @NotNull CurrentMessages currentMessages,
                @NotNull MessageBus messageBus
        ) {

            isBCompleted = true
        }

    @Override
    void onError(@NotNull MessageEnvelope error, @NotNull MessageBus messageBus) {
        super.onError(error, messageBus)
    }
}

    @CompileStatic
    class CompleteC implements AsyncMessageCallback {

        void onComplete(@NotNull String sourceId, long sourceVersion, @NotNull CurrentMessages currentMessages, @NotNull MessageBus messageBus) {
            isCCompleted = true
        }
    }
}



class Event_A_1 {}
class Event_B_1 {}
class Event_B_2 {}
class Event_C_1 {}
class Error_C_1 {}
