package pl.javorex.poc.istio.common.message.listener

import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope

interface MessageUniquenessCallback {
    fun onFirst(message: MessageEnvelope, messageBus: MessageBus)

    fun onUniqueViolated(error: MessageEnvelope, messageBus: MessageBus)
}