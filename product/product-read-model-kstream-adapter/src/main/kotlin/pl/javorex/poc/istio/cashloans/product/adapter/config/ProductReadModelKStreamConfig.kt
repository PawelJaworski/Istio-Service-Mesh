package pl.javorex.poc.istio.cashloans.product.adapter.config

import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost
import pl.javorex.poc.istio.cashloans.product.adapter.ProductQueryKStream
import pl.javorex.poc.istio.common.message.listener.MessageListener

class ProductReadModelKStreamConfig(
        private val bootstrapServers: String,
        private val instalmentTopic: String,
        private val instalmentErrorTopic: String,
        private val loanTopic: String,
        private val getLoanCost: MessageListener<GetLoanCost>
) {
    fun productQueryKStream() =
            ProductQueryKStream(
                    bootstrapServers,
                    instalmentTopic,
                    instalmentErrorTopic,
                    loanTopic,
                    getLoanCost
            )
}