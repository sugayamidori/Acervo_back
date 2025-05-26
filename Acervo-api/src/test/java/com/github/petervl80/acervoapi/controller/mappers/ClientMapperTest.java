package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.ClientDTO;
import com.github.petervl80.acervoapi.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;
class ClientMapperTest {

    private ClientMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ClientMapper.class);
    }

    @Test
    void deveConverterDTOParaEntity() {
        ClientDTO dto = new ClientDTO("client-id", "client-secret", "localhost?rolback", "CLIENT");

        Client entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.clientId(), entity.getClientId());
        assertEquals(dto.clientSecret(), entity.getClientSecret());
        assertEquals(dto.redirectURI(), entity.getRedirectURI());
        assertEquals(dto.scope(), entity.getScope());
    }

    @Test
    void deveConverterEntityParaDTO() {
        Client entity = new Client();
        entity.setClientId("client-id");
        entity.setClientSecret("client-secret");
        entity.setRedirectURI("localhost/rolback");
        entity.setScope("CLIENT");

        ClientDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getClientId(), dto.clientId());
        assertEquals(entity.getClientSecret(), dto.clientSecret());
        assertEquals(entity.getRedirectURI(), dto.redirectURI());
        assertEquals(entity.getScope(), dto.scope());
    }
}