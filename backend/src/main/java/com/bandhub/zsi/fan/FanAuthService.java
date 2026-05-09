package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.LoginRequest;
import com.bandhub.zsi.fan.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * Logowanie fana po stronie backendu (proxy do Keycloak token endpointu).
 *
 * Mobilka NIE rozmawia bezposrednio z Keycloakiem - inaczej musielibysmy konfigurowac
 * Web Origins na kliencie publicznym i ryzykowac CORS-blokade na realmie. Backend ma
 * juz poprawny CORS dla `localhost:*`, dlatego wystawiamy /api/public/auth/login i
 * przekazujemy parametry password granta na serwerze.
 *
 * Wymaga `Direct Access Grants Enabled = ON` na kliencie `bandhub-public-client`.
 */
@Service
public class FanAuthService {

    private static final Logger log = LoggerFactory.getLogger(FanAuthService.class);

    private final RestClient restClient;
    private final String tokenEndpoint;
    private final String clientId;

    public FanAuthService(
            RestClient.Builder restClientBuilder,
            @Value("${app.keycloak.issuer-uri}") String issuerUri,
            @Value("${app.keycloak.public-client-id}") String clientId
    ) {
        this.restClient = restClientBuilder.build();
        this.tokenEndpoint = issuerUri + "/protocol/openid-connect/token";
        this.clientId = clientId;
    }

    public LoginResponse login(LoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.username());
        form.add("password", request.password());
        form.add("scope", "openid profile email");

        try {
            Map<String, Object> tokenPayload = restClient.post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(form)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

            if (tokenPayload == null || tokenPayload.get("access_token") == null) {
                throw new IllegalArgumentException("Logowanie nieudane: brak tokenu w odpowiedzi z Keycloak.");
            }

            return new LoginResponse(
                    String.valueOf(tokenPayload.get("access_token")),
                    objectAsString(tokenPayload.get("refresh_token")),
                    objectAsInteger(tokenPayload.get("expires_in")),
                    objectAsString(tokenPayload.get("token_type"))
            );
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            String body = ex.getResponseBodyAsString();
            log.warn("Logowanie fana '{}' nieudane (Keycloak {} {})", request.username(), status.value(), body);
            throw new IllegalArgumentException(translateLoginError(status.value(), body));
        }
    }

    private String translateLoginError(int status, String body) {
        String safeBody = body == null ? "" : body.toLowerCase();
        if (status == 401 || safeBody.contains("invalid_grant")) {
            return "Nieprawidlowa nazwa uzytkownika lub haslo.";
        }
        if (safeBody.contains("account is not fully set up") || safeBody.contains("required action")) {
            return "Konto wymaga dokonczenia konfiguracji w panelu admina (akcje wymagane).";
        }
        if (safeBody.contains("account disabled")) {
            return "Konto zostalo wylaczone przez administratora.";
        }
        if (safeBody.contains("invalid_client")) {
            return "Klient OAuth nie jest skonfigurowany (Direct Access Grants).";
        }
        return "Logowanie nieudane (Keycloak " + status + ")";
    }

    private static String objectAsString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Integer objectAsInteger(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
