package com.bandhub.zsi.user;

import com.bandhub.zsi.user.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    private static final String REALM = "bandhub-realm";

    private final Keycloak keycloak;

    UserAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    private RealmResource realm() {
        return keycloak.realm(REALM);
    }

    public List<UserResponse> getAllUsers() {
        List<UserRepresentation> users = realm().users().list();
        return users.stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse getUser(String id) {
        UserResource userResource = realm().users().get(id);
        try {
            UserRepresentation user = userResource.toRepresentation();
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listAll();
            List<String> roleNames = roles.stream().map(RoleRepresentation::getName).toList();
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.isEnabled(),
                    user.getCreatedTimestamp() != null ? user.getCreatedTimestamp() : 0L,
                    roleNames
            );
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + id);
        }
    }

    public String createUser(CreateUserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setEnabled(request.enabled());
        user.setEmailVerified(false);

        UsersResource users = realm().users();
        try (var response = users.create(user)) {
            if (response.getStatus() == 201) {
                String id = getCreatedId(response);
                if (id != null && request.password() != null && !request.password().isBlank()) {
                    resetPassword(id, new ResetPasswordRequest(request.password(), true));
                }
                return id;
            }
            if (response.getStatus() == 409) {
                throw new IllegalArgumentException("Użytkownik o podanej nazwie już istnieje");
            }
            throw new IllegalArgumentException("Nie udało się utworzyć użytkownika");
        }
    }

    public void resetPassword(String userId, ResetPasswordRequest request) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.password());
        cred.setTemporary(request.temporary());

        try {
            realm().users().get(userId).resetPassword(cred);
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public void setEnabled(String userId, boolean enabled) {
        try {
            UserResource userResource = realm().users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public void updateUser(String userId, String firstName, String lastName, String email) {
        try {
            UserResource userResource = realm().users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (email != null) user.setEmail(email);
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public List<RoleResponse> getRealmRoles() {
        return realm().roles().list().stream()
                .map(r -> new RoleResponse(r.getId(), r.getName(), r.getDescription(), r.isComposite()))
                .toList();
    }

    public List<RoleResponse> getUserRoles(String userId) {
        try {
            return realm().users().get(userId).roles().realmLevel().listAll().stream()
                    .map(r -> new RoleResponse(r.getId(), r.getName(), r.getDescription(), r.isComposite()))
                    .toList();
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public void assignRole(String userId, String roleName) {
        RoleRepresentation role = realm().roles().get(roleName).toRepresentation();
        try {
            realm().users().get(userId).roles().realmLevel().add(List.of(role));
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public void removeRole(String userId, String roleName) {
        RoleRepresentation role = realm().roles().get(roleName).toRepresentation();
        try {
            realm().users().get(userId).roles().realmLevel().remove(List.of(role));
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public String createRole(CreateRoleRequest request) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(request.name());
        role.setDescription(request.description());
        try {
            realm().roles().create(role);
            return realm().roles().get(request.name()).toRepresentation().getId();
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 409) {
                throw new IllegalArgumentException("Rola o podanej nazwie już istnieje");
            }
            throw new IllegalArgumentException("Nie udało się utworzyć roli");
        }
    }

    public List<GroupResponse> getGroups() {
        return realm().groups().groups().stream()
                .map(g -> new GroupResponse(g.getId(), g.getName(), g.getPath()))
                .toList();
    }

    public List<GroupResponse> getUserGroups(String userId) {
        try {
            return realm().users().get(userId).groups().stream()
                    .map(g -> new GroupResponse(g.getId(), g.getName(), g.getPath()))
                    .toList();
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    public String createGroup(CreateGroupRequest request) {
        GroupRepresentation group = new GroupRepresentation();
        group.setName(request.name());
        group.setPath(request.path() != null ? request.path() : "/" + request.name());
        try (var response = realm().groups().add(group)) {
            if (response.getStatus() == 201) {
                String id = getCreatedId(response);
                return id != null ? id : "";
            }
            if (response.getStatus() == 409) {
                throw new IllegalArgumentException("Grupa o podanej nazwie już istnieje");
            }
            throw new IllegalArgumentException("Nie udało się utworzyć grupy");
        }
    }

    public void assignGroup(String userId, String groupId) {
        try {
            realm().users().get(userId).joinGroup(groupId);
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik lub grupa nie znaleziona");
        }
    }

    public void removeGroup(String userId, String groupId) {
        try {
            realm().users().get(userId).leaveGroup(groupId);
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik lub grupa nie znaleziona");
        }
    }

    public void deleteUser(String userId) {
        try {
            realm().users().get(userId).remove();
        } catch (NotFoundException e) {
            throw new EntityNotFoundException("Użytkownik nie znaleziony: " + userId);
        }
    }

    private UserResponse toUserResponse(UserRepresentation user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEnabled(),
                user.getCreatedTimestamp() != null ? user.getCreatedTimestamp() : 0L,
                List.of()
        );
    }

    private String getCreatedId(jakarta.ws.rs.core.Response response) {
        java.net.URI location = response.getLocation();
        if (location != null) {
            String path = location.getPath();
            int lastSlash = path.lastIndexOf('/');
            return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        }
        return null;
    }
}
