package com.bandhub.zsi.user.adapter;

import com.bandhub.zsi.user.UserAdminService;
import com.bandhub.zsi.user.dto.CreateRoleRequest;
import com.bandhub.zsi.user.dto.RoleResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
class RoleAdminController {

    private final UserAdminService service;

    RoleAdminController(UserAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<RoleResponse>> getRealmRoles() {
        return ResponseEntity.ok(service.getRealmRoles());
    }

    @PostMapping
    ResponseEntity<Void> createRole(@RequestBody @Valid CreateRoleRequest request) {
        String id = service.createRole(request);
        return ResponseEntity.created(URI.create("/api/admin/roles/" + id)).build();
    }
}
