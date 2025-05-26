package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MultaRepository extends JpaRepository<Multa, UUID> {
    Optional<Multa> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);
}
