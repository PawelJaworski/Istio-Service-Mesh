package pl.javorex.poc.istio.cashloans.instalment.application.integration

import java.math.BigDecimal

interface ProductReadClient {
    fun getLoanCost(productName: String): BigDecimal
}