package pl.javorex.poc.istio.common.message.async

import java.time.Duration

class AsyncMessagesBuilder {

    private var timeout: Long = 0
    private val messagesPrototype: CurrentMessages = messagesOf()

    fun withTimeout(timeout: Duration): AsyncMessagesBuilder {
        this.timeout = timeout.toMillis()

        return this
    }

    fun startsWith(clazz: Class<*>): AsyncMessagesBuilder {
        messagesPrototype.starting[clazz.simpleName] = LACK_OF_MESSAGE

        return this
    }
    fun requires(clazz: Class<*>): AsyncMessagesBuilder {
        messagesPrototype.required[clazz.simpleName] = LACK_OF_MESSAGE

        return this
    }
    fun expectErrors(clazz: Class<*>): AsyncMessagesBuilder {
        messagesPrototype.expectedErrors += clazz.simpleName

        return this
    }

    fun build() : AsyncMessagesTemplate {
        val messages = messagesOf(messagesPrototype)

        return AsyncMessagesTemplate(timeout).updateMessages(messages)
    }
}