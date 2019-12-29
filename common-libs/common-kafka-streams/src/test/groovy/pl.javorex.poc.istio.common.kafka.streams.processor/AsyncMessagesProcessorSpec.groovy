package pl.javorex.poc.istio.common.kafka.streams.processor

import groovy.transform.CompileStatic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.test.TestRecord
import pl.javorex.poc.istio.common.kafka.streams.JsonPOJODeserializer
import pl.javorex.poc.istio.common.kafka.streams.JsonPOJOSerializer
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeDeserializer
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelopeKt
import spock.lang.Specification
import org.apache.kafka.streams.TopologyTestDriver

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

import static pl.javorex.poc.istio.common.message.envelope.MessageEnvelopeKt.pack

class AsyncMessagesProcessorSpec extends Specification {
    static final String TOPIC_A = "topicA"
    static final String ERROR_TOPIC_A = "errorTopicA"
    static final String TOPIC_B = "topicB"
    static final String ERROR_TOPIC_B = "errorTopicB"
    static final String TOPIC_C = "topicC"
    static final String ERROR_TOPIC_C = "errorTopicC"

    Topology topology
    Properties config

    def setup() {
         topology = new ExampleTopology(TOPIC_A, ERROR_TOPIC_A, TOPIC_B, ERROR_TOPIC_B, TOPIC_C, ERROR_TOPIC_C)
            .create()

// setup test driver
        config = new Properties();
        config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde.class
        config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde.class

    }

    def "test test"() {
        given:
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, ExampleTopology.config);
        when:
        TestInputTopic<String, MessageEnvelope> inputTopic = testDriver
                .createInputTopic(TOPIC_A, new StringSerializer(), new JsonPOJOSerializer<MessageEnvelope>());

        TestRecord<String, MessageEnvelope> testRecord = new TestRecord<>("key1", pack("key1", 1, new Event_A_1()))
        testRecord.headers().add("transactionId", "1".bytes)
        inputTopic.pipeInput(testRecord)

        testDriver.advanceWallClockTime(Duration.ofSeconds(10))

        TestRecord<String, MessageEnvelope> testRecord2 = new TestRecord<>("key2", pack("key2", 2, new Event_A_1()))
        testRecord.headers().add("transactionId", "2".bytes)
        inputTopic.pipeInput(testRecord2)

        testDriver.advanceWallClockTime(Duration.ofMinutes(5))
        TestOutputTopic<String, MessageEnvelope> outputTopic = testDriver
                .createOutputTopic(ERROR_TOPIC_A, new StringDeserializer(), new MessageEnvelopeDeserializer())
        outputTopic.readRecordsToList()
                .forEach{
                    println("${it.value().asString()}")
                    println("${it.headers()}")
                    println("--------------------------------------------------")
        }


        then:
        true
    }
}
