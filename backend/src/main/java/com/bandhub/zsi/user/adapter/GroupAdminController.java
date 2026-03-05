package com.bandhub.zsi.user.adapter;

import com.bandhub.zsi.user.UserAdminService;
import com.bandhub.zsi.user.dto.CreateGroupRequest;
import com.bandhub.zsi.user.dto.GroupResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/groups")
@PreAuthorize("hasRole('ADMIN')")
class GroupAdminController {

    private final UserAdminService service;

    GroupAdminController(UserAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<GroupResponse>> getGroups() {
        return ResponseEntity.ok(service.getGroups());
    }

    @PostMapping
    ResponseEntity<Void> createGroup(@RequestBody @Valid CreateGroupRequest request) {
        String id = service.createGroup(request);
        return ResponseEntity.created(URI.create("/api/admin/groups/" + id)).build();
    }
}
