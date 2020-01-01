package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.test.TestRecord
import pl.javorex.poc.istio.common.kafka.streams.JsonPOJOSerializer
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeDeserializer
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import spock.lang.Shared
import spock.lang.Specification
import org.apache.kafka.streams.TopologyTestDriver

import java.time.Duration

import static pl.javorex.poc.istio.common.message.envelope.MessageEnvelopeKt.pack

class AsyncMessagesProcessorSpec extends Specification {
    static final StringSerializer KEY_SERIALIZER = new StringSerializer()
    static final JsonPOJOSerializer<MessageEnvelope> VALUE_SERIALIZER = new JsonPOJOSerializer<MessageEnvelope>()

    static final String HEADER_TRANSACTION_ID = "transactionId"

    static final String KEY_1 = "key1"
    static final String KEY_2 = "key2"

    static final String TOPIC_A = "topicA"
    static final String TOPIC_B = "topicB"
    static final String TOPIC_C = "topicC"
    static final String ERROR_TOPIC = "errorTopic"

    Properties config

    @Shared
    TopologyTestDriver testDriver

    @Shared
    TestInputTopic<String, MessageEnvelope> inputA
    @Shared
    TestInputTopic<String, MessageEnvelope> inputError

    @Shared
    TestOutputTopic<String, MessageEnvelope> outputError

    ExampleTopology testedTopology

    def setup() {
        if (testDriver != null) {
            testDriver.close()
        }
        testedTopology = new ExampleTopology(TOPIC_A, TOPIC_B, TOPIC_C, ERROR_TOPIC)
        Topology topology = testedTopology.create()
        testDriver = new TopologyTestDriver(topology, ExampleTopology.config)
        inputA = testDriver.createInputTopic(TOPIC_A, KEY_SERIALIZER, VALUE_SERIALIZER)
        inputError = testDriver
                .createInputTopic(ERROR_TOPIC, new StringSerializer(), new JsonPOJOSerializer<MessageEnvelope>())

        outputError = testDriver
                .createOutputTopic(ERROR_TOPIC, new StringDeserializer(), new MessageEnvelopeDeserializer())
        config = new Properties();
        config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde.class
        config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde.class
    }

    def "test test"() {
        when:
        TestRecord<String, MessageEnvelope> testRecord = new TestRecord<>(KEY_1, pack(KEY_1, 1, new Event_A_1()))
        testRecord.headers().add(HEADER_TRANSACTION_ID, "1".bytes)
        inputA.pipeInput(testRecord)

        testDriver.advanceWallClockTime(Duration.ofSeconds(10))

        TestRecord<String, MessageEnvelope> testRecord2 = new TestRecord<>(KEY_2, pack(KEY_2, 2, new Event_A_1()))
        testRecord.headers().add(HEADER_TRANSACTION_ID, "2".bytes)
        inputA.pipeInput(testRecord2)

        testDriver.advanceWallClockTime(Duration.ofMinutes(5))

//        outputError.readRecordsToList()
//                .forEach{
//                    println("${it.value().asString()}")
//                    println("${it.headers()}")
//                    println("--------------------------------------------------")
//        }

        then:
        true
    }

    def "should fail on error"() {
        when:
        TestRecord<String, MessageEnvelope> testRecord = new TestRecord<>(KEY_1, pack(KEY_1, 1, new Event_A_1()))
        testRecord.headers().add(HEADER_TRANSACTION_ID, "1".bytes)
        inputA.pipeInput(testRecord)

        testDriver.advanceWallClockTime(Duration.ofSeconds(10))

        TestRecord<String, MessageEnvelope> error = new TestRecord<>(KEY_1, pack(KEY_1, 1, "test-error"))
        error.headers().add(HEADER_TRANSACTION_ID, "1".bytes)
        inputError.pipeInput(error)

        testDriver.advanceWallClockTime(Duration.ofMinutes(5))

        then:
        !testedTopology.isBCompleted
        testedTopology.errorB == "messaging.failure.runtimeError"
    }

    def "should failed on double message"() {
        when:
        TestRecord<String, MessageEnvelope> record1 = new TestRecord<>(KEY_1, pack(KEY_1, 1, new Event_A_1()))
        record1.headers().add(HEADER_TRANSACTION_ID, "1".bytes)
        inputA.pipeInput(record1)

        testDriver.advanceWallClockTime(Duration.ofSeconds(1))
        TestRecord<String, MessageEnvelope> record2 = new TestRecord<>(KEY_1, pack(KEY_1, 1, new Event_A_1()))
        record2.headers().add(HEADER_TRANSACTION_ID, "1".bytes)
        inputA.pipeInput(record2)

        then:
        !testedTopology.isBCompleted
        testedTopology.errorB == "messaging.failure.doubleMessage"
    }

    def "should failed on concurrent modification"(TestRecord<String, MessageEnvelope>[] inputsForA) {
        when:
        inputsForA.toList().forEach{
            inputA.pipeInput(it)
            testDriver.advanceWallClockTime(Duration.ofSeconds(1))
        }

        then:
        !testedTopology.isBCompleted
        testedTopology.errorB == "messaging.failure.concurrentModification"

        where:
        inputsForA << [[
                new TestRecord<>(KEY_1, pack(KEY_1, 1, new Event_A_1())),
                new TestRecord<>(KEY_1, pack(KEY_1, 2, new Event_B_1()))
        ]]
    }
}
