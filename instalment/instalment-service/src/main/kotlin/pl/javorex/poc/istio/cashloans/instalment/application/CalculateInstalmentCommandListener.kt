package pl.javorex.poc.istio.cashloans.instalment.application

import pl.javorex.poc.istio.cashloans.instalment.application.command.CalculateInstalment
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.listener.MessageListener

class CalculateInstalmentCommandListener(
    private val instalmentCommandFacade: InstalmentCommandFacade
) : MessageListener<CalculateInstalment> {

    override fun onMessage(sourceId: String, sourceVersion: Long, calculateInstalment: CalculateInstalment,
                           messageBus: MessageBus
    ) {
        instalmentCommandFacade.calculateInstalment(calculateInstalment, messageBus)
    }
}