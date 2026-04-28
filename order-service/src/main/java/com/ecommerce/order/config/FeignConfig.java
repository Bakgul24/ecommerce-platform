package com.ecommerce.order.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.ecommerce.order.client")
public class FeignConfig {
}