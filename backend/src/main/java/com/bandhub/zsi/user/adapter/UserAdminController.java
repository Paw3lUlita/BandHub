package com.bandhub.zsi.user.adapter;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final Keycloak keycloak;

    public UserAdminController(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @GetMapping
    public List<UserRepresentation> getAllUsers() {
        return keycloak.realm("bandhub-realm")
                .users()
                .list();
    }

    @GetMapping("/{id}/roles")
    public List<RoleRepresentation> getUserRoles(@PathVariable String id) {
        return keycloak.realm("bandhub-realm")
                .users()
                .get(id)
                .roles()
                .realmLevel()
                .listAll();
    }
}
