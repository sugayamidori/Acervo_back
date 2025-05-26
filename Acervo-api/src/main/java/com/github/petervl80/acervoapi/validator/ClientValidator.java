package com.github.petervl80.acervoapi.validator;

import com.github.petervl80.acervoapi.exceptions.RegistroDuplicadoException;
import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final ClientRepository repository;

    public void validar(Client client) {
        if(existeCLientId(client)) {
            throw new RegistroDuplicadoException("Client já cadastrado");
        }
    }

    private boolean existeCLientId(Client client) {
        Optional<Client> clientEncontrado = Optional.ofNullable(repository.findByClientId(client.getClientId()));

        if(client.getId() == null) {
            return clientEncontrado.isPresent();
        }

        return clientEncontrado
                .map(Client::getId)
                .stream()
                .anyMatch( id -> !id.equals(client.getId()));
    }
}
