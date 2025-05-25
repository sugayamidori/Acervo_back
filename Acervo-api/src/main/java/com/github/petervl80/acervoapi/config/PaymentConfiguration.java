package com.github.petervl80.acervoapi.config;
import com.mercadopago.MercadoPagoConfig;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class PaymentConfiguration {
    @PostConstruct
    public void init() {
        //VOLTAR PRA CÁ BOTAR NO .ENV
        MercadoPagoConfig.setAccessToken("TEST-6817096861883625-052120-fafb78569ec128ca185d5f86b9cee0a9-594803070");
    }
}
