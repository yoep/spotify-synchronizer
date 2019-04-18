package org.synchronizer.spotify;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry
@Configuration
public class RetryConfiguration {
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();

        retryPolicy.setMaxAttempts(300);
        backOffPolicy.setBackOffPeriod(1000);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    @Bean
    public RetryOperationsInterceptor spotifyRetry(RetryTemplate retryTemplate) {
        return RetryInterceptorBuilder.stateless().retryOperations(retryTemplate).build();
    }
}
