package pl.javorex.poc.istio.cashloans.product.application

import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost
import pl.javorex.poc.istio.cashloans.product.domain.ProductRepository

class ProductQueryFacade(private val productRepository: ProductRepository) {

    fun getLoanCost(command: GetLoanCost) =
        productRepository
            .findProductByName(command.productName)
            .loanCost
}