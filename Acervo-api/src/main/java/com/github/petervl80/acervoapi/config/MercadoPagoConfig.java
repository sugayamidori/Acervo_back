package com.github.petervl80.acervoapi.config;

import com.mercadopago.client.payment.PaymentClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MercadoPagoConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
