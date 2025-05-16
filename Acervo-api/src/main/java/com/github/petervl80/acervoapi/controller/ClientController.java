package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.ClientDTO;
import com.github.petervl80.acervoapi.controller.mappers.ClientMapper;
import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController implements GenericController {

    private final ClientService service;
    private final ClientMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> salvar(@RequestBody ClientDTO dto) {
        log.info("Registrando novo Client: {} com scope: {}", dto.clientId(), dto.scope());
        Client client = mapper.toEntity(dto);
        service.salvar(client);

        URI location = gerarHeaderLocation(client.getId());

        return ResponseEntity.created(location).build();

    }
}
