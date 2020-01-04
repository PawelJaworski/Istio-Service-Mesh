package pl.javorex.poc.istio.common.kafka.streams.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
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

import java.nio.ByteBuffer
import java.time.Duration

import static pl.javorex.poc.istio.common.message.envelope.MessageEnvelopeKt.pack

class AsyncMessagesProcessorSpec extends Specification {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

    static final StringSerializer KEY_SERIALIZER = new StringSerializer()
    static final JsonPOJOSerializer<MessageEnvelope> VALUE_SERIALIZER = new JsonPOJOSerializer<MessageEnvelope>()

    static final String HEADER_TRANSACTION_ID = "transactionId"

    static final String KEY_1 = "key1"
    static final long TRANSACTION_1 = 1
    static final String KEY_2 = "key2"
    static final long TRANSACTION_2 = 2


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

    def "should fail on error"() {
        when:
        TestRecord<String, MessageEnvelope> testRecord = new TestRecordBuilder(
                KEY_1,
                TRANSACTION_1,
                new Event_A_1()
        ).build()
        inputA.pipeInput(testRecord)

        testDriver.advanceWallClockTime(Duration.ofSeconds(10))

        TestRecord<String, MessageEnvelope> error = new TestRecordBuilder(
                KEY_1,
                TRANSACTION_1,
                "test-error"
        ).build()
        inputError.pipeInput(error)

        testDriver.advanceWallClockTime(Duration.ofMinutes(5))

        then:
        println("$testedTopology.errorB")
        !testedTopology.isBCompleted
        testedTopology.errorB == "messaging.failure.runtimeError"
    }

    def "should failed on double message"() {
        when:
        TestRecord<String, MessageEnvelope> record1 = new TestRecordBuilder(
                KEY_1,
                TRANSACTION_1,
                new Event_A_1()
        ).build()
        inputA.pipeInput(record1)

        testDriver.advanceWallClockTime(Duration.ofSeconds(1))
        TestRecord<String, MessageEnvelope> record2 = new TestRecordBuilder(
                KEY_1,
                TRANSACTION_1,
                new Event_A_1()
        ).build()
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
            new TestRecordBuilder(KEY_1, 1, new Event_A_1())
                    .build(),
            new TestRecordBuilder(KEY_1, 2, new Event_B_1())
                    .build()
        ]]
    }

    def "should complete"() {
        given:
        TestRecord<String, MessageEnvelope> recordA1 = new TestRecordBuilder(KEY_1, TRANSACTION_1, new Event_A_1())
                .build()
        TestRecord<String, MessageEnvelope> recordB1 = new TestRecordBuilder(KEY_1, TRANSACTION_1, new Event_B_1())
                .build()
        TestRecord<String, MessageEnvelope> recordB2 = new TestRecordBuilder(KEY_1, TRANSACTION_1, new Event_B_2())
                .build()
        TestRecord<String, MessageEnvelope> recordC1 = new TestRecordBuilder(KEY_1, TRANSACTION_1, new Event_C_1())
                .build()

        when:
        inputA.pipeInput(recordA1)
        testDriver.advanceWallClockTime(Duration.ofMillis(1))
        inputA.pipeInput(recordB1)
        testDriver.advanceWallClockTime(Duration.ofMillis(1))
        inputA.pipeInput(recordB2)
        testDriver.advanceWallClockTime(Duration.ofMillis(1))
        inputA.pipeInput(recordC1)

        then:
        testedTopology.isBCompleted
        testedTopology.isCCompleted
    }
}

class TestRecordBuilder {
    private TestRecord<String, MessageEnvelope> testRecord

    TestRecordBuilder(String key, long transactionId, Object message) {
        this.testRecord = new TestRecord<>(
                key,
                AsyncMessagesProcessorSpec.OBJECT_MAPPER.writeValueAsString(message)
        )
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(transactionId);
        byte[] asBytes = buffer.array();
        this.testRecord.headers().add("transactionId", asBytes)
        this.testRecord.headers().add("messageType", message.class.simpleName.bytes)
    }

    TestRecord<String, MessageEnvelope> build() {
        return testRecord
    }
}
