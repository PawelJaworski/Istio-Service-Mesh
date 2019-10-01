package pl.javorex.poc.istio.cashloans.product.adapter

import pl.javorex.poc.istio.cashloans.product.domain.LoanProduct
import pl.javorex.poc.istio.cashloans.product.domain.ProductRepository
import java.math.BigDecimal

class ProductRepositoryInMemoryImpl : ProductRepository {

    override fun findProductByName(productName: String): LoanProduct {
        check(productName == "GREAT_PRODUCT") {
            "Cannot find product $productName"
        }

        return LoanProduct(
            loanCost = BigDecimal.valueOf(12)
        )
    }
}