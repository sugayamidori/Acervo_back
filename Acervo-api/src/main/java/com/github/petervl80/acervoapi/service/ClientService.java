package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.ClientRepository;
import com.github.petervl80.acervoapi.validator.ClientValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final ClientValidator validator;
    private final PasswordEncoder encoder;
    private final RestTemplate restTemplate;

    public Client salvar(Client client) {
        validator.validar(client);
        String senhaCriptografada = encoder.encode(client.getClientSecret());
        client.setClientSecret(senhaCriptografada);
        return repository.save(client);
    }

    public Client obterPorClientID(String clientId){
        return repository.findByClientId(clientId);
    }

    public String getTokenFromOAuth(Usuario usuario) {
        String context = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

        Client client = repository.findByScopeInAndRedirectURIContaining(usuario.getRoles(), context);
        String tokenUrl = context + "/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String secret = client.getClientId().substring(client.getClientId().indexOf("-") + 1) + "-secret";
        headers.setBasicAuth(client.getClientId(), secret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", client.getScope());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        return response.getBody();
    }
}
