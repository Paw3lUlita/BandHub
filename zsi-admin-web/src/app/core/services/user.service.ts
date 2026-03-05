import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserResponse {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  enabled: boolean;
  createdTimestamp: number;
  roles: string[];
}

export interface CreateUserRequest {
  username: string;
  password: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  enabled: boolean;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
}

export interface ResetPasswordRequest {
  password: string;
  temporary: boolean;
}

export interface RoleResponse {
  id: string;
  name: string;
  description?: string;
  composite: boolean;
}

export interface CreateRoleRequest {
  name: string;
  description?: string;
}

export interface AssignRoleRequest {
  roleName: string;
}

export interface GroupResponse {
  id: string;
  name: string;
  path: string;
}

export interface CreateGroupRequest {
  name: string;
  path?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private usersUrl = 'http://localhost:8080/api/admin/users';
  private rolesUrl = 'http://localhost:8080/api/admin/roles';
  private groupsUrl = 'http://localhost:8080/api/admin/groups';

  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.usersUrl);
  }

  getUser(id: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.usersUrl}/${id}`);
  }

  createUser(req: CreateUserRequest): Observable<void> {
    return this.http.post<void>(this.usersUrl, req);
  }

  updateUser(id: string, req: UpdateUserRequest): Observable<void> {
    return this.http.put<void>(`${this.usersUrl}/${id}`, req);
  }

  resetPassword(id: string, req: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.usersUrl}/${id}/reset-password`, req);
  }

  setEnabled(id: string, enabled: boolean): Observable<void> {
    return this.http.put<void>(`${this.usersUrl}/${id}/enabled`, { enabled });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.usersUrl}/${id}`);
  }

  getUserRoles(id: string): Observable<RoleResponse[]> {
    return this.http.get<RoleResponse[]>(`${this.usersUrl}/${id}/roles`);
  }

  assignRole(userId: string, roleName: string): Observable<void> {
    return this.http.post<void>(`${this.usersUrl}/${userId}/roles`, { roleName });
  }

  removeRole(userId: string, roleName: string): Observable<void> {
    return this.http.delete<void>(`${this.usersUrl}/${userId}/roles/${encodeURIComponent(roleName)}`);
  }

  getUserGroups(id: string): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>(`${this.usersUrl}/${id}/groups`);
  }

  assignGroup(userId: string, groupId: string): Observable<void> {
    return this.http.post<void>(`${this.usersUrl}/${userId}/groups/${groupId}`, {});
  }

  removeGroup(userId: string, groupId: string): Observable<void> {
    return this.http.delete<void>(`${this.usersUrl}/${userId}/groups/${groupId}`);
  }

  getRealmRoles(): Observable<RoleResponse[]> {
    return this.http.get<RoleResponse[]>(this.rolesUrl);
  }

  createRole(req: CreateRoleRequest): Observable<void> {
    return this.http.post<void>(this.rolesUrl, req);
  }

  getGroups(): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>(this.groupsUrl);
  }

  createGroup(req: CreateGroupRequest): Observable<void> {
    return this.http.post<void>(this.groupsUrl, req);
  }
}
