package pl.javorex.poc.istio.util.kafka.streams.processor

import groovy.transform.CompileStatic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.jetbrains.annotations.NotNull
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback

import java.time.Duration

@CompileStatic
class ExampleTopology {
    public static final Properties config = new Properties()

    final String topicA
    final String topicB
    final String topicC
    final String errorTopic

    final Topology topology

    boolean isBCompleted
    List<String> errorB = new ArrayList<>()
    boolean isCCompleted
    List<String> errorC = new ArrayList<>()

    static {
        config[StreamsConfig.APPLICATION_ID_CONFIG] = "Create-Loan-Stream"
        config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "0.0.0.0:0001"
        config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde.class
        config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.StringSerde.class
        config[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        config[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000
    }

    ExampleTopology(String topicA, String topicB, String topicC, String errorTopic) {
        this.topicA = topicA
        this.topicB = topicB
        this.topicC = topicC
        this.errorTopic = errorTopic

        topology = create()
    }

    Topology create() {
        Topology topology = createTopology()

        AsyncMessagesSaga saga = new AsyncMessagesSaga()
        saga.timeout(Duration.ofSeconds(30))
        saga.topic(topicA, errorTopic)
        saga.startsWith(Event_A_1.class)

        saga.step()
                .named("fetch_B-1_and_B-2")
                .heartBeat(HeartBeatInterval.ofSeconds(2))
                .sources(sourceFrom(topicA), sourceFrom(topicB), sourceFrom(errorTopic))
                .requires(Event_B_1)
                .requires(Event_B_2)
                .onComplete(new CompleteB())
        saga.step()
                .named("fetch_C-1")
                .heartBeat(HeartBeatInterval.ofSeconds(2))
                .sources(sourceFrom(topicA), sourceFrom(topicC), sourceFrom(errorTopic))
                .requires(Event_C_1)
                .onComplete(new CompleteC())

        saga.joinInto(topology)

       return topology
    }

    private Topology createTopology() {
        new StreamsBuilder().build()
                .addSource(sourceFrom(topicA), topicA)
                .addSource(sourceFrom(topicB), topicB)
                .addSource(sourceFrom(topicC), topicC)
                .addSource(sourceFrom(errorTopic), errorTopic)
    }

    private String sourceFrom(String topic) {
        "$topic-source"
    }

    class CompleteB implements AsyncMessageCallback<String> {
        @Override
        void onComplete(
                @NotNull String sourceId,
                long sourceVersion,
                @NotNull CurrentMessages<String> currentMessages,
                @NotNull MessageBus<String> messageBus
        ) {

            isBCompleted = true
        }

        @Override
        void onFailure(@NotNull String aggregateId, long transactionId, @NotNull String errorCode,
                     @NotNull MessageBus<String> messageBus) {
            String message = "$transactionId.$errorCode"
            errorB.add(message)
        }

        void onTimeout(@NotNull String sourceId, long sourceVersion, @NotNull CurrentMessages<String> currentMessages, @NotNull MessageBus<String> messageBus) {
            List<String> missingEvents = currentMessages.missing()
            String message = "Timeout. Missing: $missingEvents"
            errorB.add(message)
            messageBus.emitError(sourceId, message)
        }
    }

    class CompleteC implements AsyncMessageCallback<String> {

        void onComplete(@NotNull String sourceId, long sourceVersion, @NotNull CurrentMessages currentMessages,
                        @NotNull MessageBus messageBus) {
            isCCompleted = true
        }

        void onFailure(@NotNull String key, long transactionId, @NotNull String errorCode, @NotNull MessageBus<String> messageBus) {
            String message = "$transactionId.$errorCode"
            errorC.add(message)
        }

        void onTimeout(@NotNull String sourceId, long sourceVersion, @NotNull CurrentMessages<String> currentMessages, @NotNull MessageBus<String> messageBus) {
            List<String> missingEvents = currentMessages.missing()
            String message = "Timeout. Missing: $missingEvents"
            errorC.add(message)
            messageBus.emitError(sourceId, message)
        }
    }
}



class Event_A_1 {
    String body = "Body of Event A_1"
}
class Event_B_1 {
    String body = "Body of Event B_1"
}
class Event_B_2 {
    String body = "Body of Event B_2"
}
class Event_C_1 {
    String body = "Body of Event C_1"
}
class Error_C_1 {
    String body = "Body of Error C_1"
}
