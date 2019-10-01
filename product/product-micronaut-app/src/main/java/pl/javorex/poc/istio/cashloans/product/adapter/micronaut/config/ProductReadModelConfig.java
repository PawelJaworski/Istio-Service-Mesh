package pl.javorex.poc.istio.cashloans.product.adapter.micronaut.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import pl.javorex.poc.istio.cashloans.product.adapter.ProductRepositoryInMemoryImpl;
import pl.javorex.poc.istio.cashloans.product.application.ProductQueryFacade;
import pl.javorex.poc.istio.cashloans.product.domain.ProductRepository;
import pl.javorex.poc.istio.cashloans.product.adapter.ProductQueryKStream;
import pl.javorex.poc.istio.cashloans.product.adapter.LoanCostFetchedCallback;
import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost;
import pl.javorex.poc.istio.common.message.listener.MessageListener;

import javax.inject.Singleton;

@Factory
public class ProductReadModelConfig {

    private final String bootstrapServers;
    private final String productTopic;
    private final String productErrorTopic;
    private final String loanTopic;

    public ProductReadModelConfig(
            @Value("${kafka.bootstrap.servers}") String bootstrapServers,
            @Value("${kafka.topic.product}") String productTopic,
            @Value("${kafka.topic.product.error}") String productErrorTopic,
            @Value("${kafka.topic.loan}") String loanTopic
    ) {
        this.bootstrapServers = bootstrapServers;
        this.productTopic = productTopic;
        this.productErrorTopic = productErrorTopic;
        this.loanTopic = loanTopic;
    }

    @EventListener
    void productQueryKStream(final ServiceStartedEvent event) {
        new ProductQueryKStream(bootstrapServers, productTopic, productErrorTopic, loanTopic,
                getLoanCost());
    }

    @Singleton
    MessageListener<GetLoanCost> getLoanCost() {
        return new LoanCostFetchedCallback(productQueryFacade());
    }

    @Singleton
    ProductQueryFacade productQueryFacade() {
        return new ProductQueryFacade(productRepository());
    }

    @Singleton
    ProductRepository productRepository() {
        return new ProductRepositoryInMemoryImpl();
    }
}
