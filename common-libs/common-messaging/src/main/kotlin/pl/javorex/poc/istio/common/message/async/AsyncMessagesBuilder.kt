package pl.javorex.poc.istio.common.message.async

import java.time.Duration

class AsyncMessagesBuilder<M> {

    private var timeout: Long = 0
    private val messagesPrototype: CurrentMessages<M> = messagesOf()

    fun withTimeout(timeout: Duration): AsyncMessagesBuilder<M> {
        this.timeout = timeout.toMillis()

        return this
    }

    fun startsWith(clazz: Class<*>): AsyncMessagesBuilder<M> {
        messagesPrototype.starting[clazz.simpleName] = LACK_OF_MESSAGE

        return this
    }

    fun requires(clazz: Class<*>): AsyncMessagesBuilder<M> {
        messagesPrototype.required[clazz.simpleName] = LACK_OF_MESSAGE

        return this
    }

    fun build() : AsyncMessagesTemplate<M> {
        val messages = messagesOf(messagesPrototype)

        return AsyncMessagesTemplate<M>(timeout).updateMessages(messages)
    }
}
