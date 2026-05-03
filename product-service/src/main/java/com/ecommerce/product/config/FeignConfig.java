package com.ecommerce.product.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.ecommerce.product.client")
public class FeignConfig {
}