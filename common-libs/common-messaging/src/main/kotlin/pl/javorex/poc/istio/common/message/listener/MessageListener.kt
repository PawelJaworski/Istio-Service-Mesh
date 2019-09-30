package pl.javorex.poc.istio.common.message.listener

import pl.javorex.poc.istio.common.message.MessageBus

interface MessageListener<M> {
    fun onMessage(sourceId: String, sourceVersion: Long, message: M, messageBus: MessageBus)
}