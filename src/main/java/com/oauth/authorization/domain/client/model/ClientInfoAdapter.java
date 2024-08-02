package com.oauth.authorization.domain.client.model;

import lombok.Getter;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Getter
public class ClientInfoAdapter extends RegisteredClient {

    private final ClientInfo clientInfo;

    public ClientInfoAdapter(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }
}
