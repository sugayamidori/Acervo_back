package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.ClientDTO;
import com.github.petervl80.acervoapi.model.Client;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-25T15:18:23-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toEntity(ClientDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Client client = new Client();

        client.setClientId( dto.clientId() );
        client.setClientSecret( dto.clientSecret() );
        client.setRedirectURI( dto.redirectURI() );
        client.setScope( dto.scope() );

        return client;
    }

    @Override
    public ClientDTO toDTO(Client client) {
        if ( client == null ) {
            return null;
        }

        String clientId = null;
        String clientSecret = null;
        String redirectURI = null;
        String scope = null;

        clientId = client.getClientId();
        clientSecret = client.getClientSecret();
        redirectURI = client.getRedirectURI();
        scope = client.getScope();

        ClientDTO clientDTO = new ClientDTO( clientId, clientSecret, redirectURI, scope );

        return clientDTO;
    }
}
