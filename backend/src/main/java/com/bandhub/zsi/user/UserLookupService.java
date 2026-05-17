package com.bandhub.zsi.user;

import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserLookupService {

    private static final String REALM = "bandhub-realm";

    private final Keycloak keycloak;

    public UserLookupService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    private RealmResource realm() {
        return keycloak.realm(REALM);
    }

    public Map<String, String> usernamesByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .filter(id -> !id.isBlank())
                .distinct()
                .collect(Collectors.toMap(id -> id, this::resolveUsername, (a, b) -> a));
    }

    public String usernameOrId(String id) {
        if (id == null || id.isBlank()) {
            return "-";
        }
        return resolveUsername(id);
    }

    private String resolveUsername(String userId) {
        try {
            UserRepresentation user = realm().users().get(userId).toRepresentation();
            String username = user.getUsername();
            if (username != null && !username.isBlank()) {
                return username;
            }
            return userId;
        } catch (NotFoundException e) {
            return userId;
        } catch (Exception e) {
            return userId;
        }
    }
}
