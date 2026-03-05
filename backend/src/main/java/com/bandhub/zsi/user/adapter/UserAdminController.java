package com.bandhub.zsi.user.adapter;

import com.bandhub.zsi.user.UserAdminService;
import com.bandhub.zsi.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
class UserAdminController {

    private final UserAdminService service;

    UserAdminController(UserAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest request) {
        String id = service.createUser(request);
        return ResponseEntity.created(URI.create("/api/admin/users/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        service.updateUser(id, request.firstName(), request.lastName(), request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    ResponseEntity<Void> resetPassword(@PathVariable String id, @RequestBody @Valid ResetPasswordRequest request) {
        service.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enabled")
    ResponseEntity<Void> setEnabled(@PathVariable String id, @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            throw new IllegalArgumentException("Pole 'enabled' jest wymagane");
        }
        service.setEnabled(id, enabled);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    ResponseEntity<List<RoleResponse>> getUserRoles(@PathVariable String id) {
        return ResponseEntity.ok(service.getUserRoles(id));
    }

    @PostMapping("/{id}/roles")
    ResponseEntity<Void> assignRole(@PathVariable String id, @RequestBody @Valid AssignRoleRequest request) {
        service.assignRole(id, request.roleName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    ResponseEntity<Void> removeRole(@PathVariable String id, @PathVariable String roleName) {
        service.removeRole(id, roleName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/groups")
    ResponseEntity<List<GroupResponse>> getUserGroups(@PathVariable String id) {
        return ResponseEntity.ok(service.getUserGroups(id));
    }

    @PostMapping("/{id}/groups/{groupId}")
    ResponseEntity<Void> assignGroup(@PathVariable String id, @PathVariable String groupId) {
        service.assignGroup(id, groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/groups/{groupId}")
    ResponseEntity<Void> removeGroup(@PathVariable String id, @PathVariable String groupId) {
        service.removeGroup(id, groupId);
        return ResponseEntity.noContent().build();
    }
}
