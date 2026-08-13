package com.example.demo.auth.controller;

import com.example.demo.auth.service.RbacService;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.SysPermission;
import com.example.demo.entity.SysRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RBAC 权限管理控制器
 * <p>
 * 提供角色 CRUD、权限查询、用户-角色分配、角色-权限分配的管理接口。
 * 所有接口需要 SUPER_ADMIN 或具有对应权限的角色才能访问。
 */
@Tag(name = "RBAC 权限管理", description = "角色/权限的增删改查、用户-角色分配")
@RestController
@RequestMapping("/api/rbac")
public class RbacController {

    private final RbacService rbacService;

    public RbacController(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    // ────────── 角色管理 ──────────

    @Operation(summary = "列出所有角色")
    @RequirePermission("role:view")
    @GetMapping("/roles")
    public Result<List<SysRole>> listRoles() {
        return Result.success(rbacService.listRoles());
    }

    @Operation(summary = "创建角色")
    @RequirePermission("role:assign")
    @PostMapping("/roles")
    public Result<SysRole> createRole(@RequestBody Map<String, String> body) {
        String roleCode = body.get("roleCode");
        String roleName = body.get("roleName");
        if (roleCode == null || roleCode.isBlank()) {
            return Result.error(400, "roleCode 不能为空");
        }
        if (roleName == null || roleName.isBlank()) {
            return Result.error(400, "roleName 不能为空");
        }
        return Result.success(rbacService.createRole(roleCode, roleName));
    }

    @Operation(summary = "更新角色")
    @RequirePermission("role:assign")
    @PutMapping("/roles/{roleId}")
    public Result<SysRole> updateRole(@PathVariable Long roleId,
                                       @RequestBody Map<String, String> body) {
        SysRole role = rbacService.updateRole(roleId,
                body.get("roleCode"), body.get("roleName"));
        return role != null ? Result.success(role) : Result.error(404, "角色不存在");
    }

    @Operation(summary = "删除角色")
    @RequirePermission("role:assign")
    @DeleteMapping("/roles/{roleId}")
    public Result<String> deleteRole(@PathVariable Long roleId) {
        return rbacService.deleteRole(roleId)
                ? Result.success("删除成功") : Result.error(404, "角色不存在");
    }

    // ────────── 权限查询 ──────────

    @Operation(summary = "列出所有权限")
    @RequirePermission("permission:view")
    @GetMapping("/permissions")
    public Result<List<SysPermission>> listPermissions() {
        return Result.success(rbacService.listPermissions());
    }

    // ────────── 用户-角色关联 ──────────

    @Operation(summary = "查询用户的角色列表")
    @RequirePermission("role:view")
    @GetMapping("/users/{userId}/roles")
    public Result<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        return Result.success(rbacService.getUserRoles(userId));
    }

    @Operation(summary = "为用户分配角色")
    @RequirePermission("role:assign")
    @PostMapping("/users/{userId}/roles")
    public Result<String> assignUserRole(@PathVariable Long userId,
                                          @RequestBody Map<String, String> body) {
        String roleCode = body.get("roleCode");
        if (roleCode == null || roleCode.isBlank()) {
            return Result.error(400, "roleCode 不能为空");
        }
        rbacService.assignRole(userId, roleCode);
        return Result.success("角色分配成功");
    }

    @Operation(summary = "移除用户的角色")
    @RequirePermission("role:assign")
    @DeleteMapping("/users/{userId}/roles/{roleCode}")
    public Result<String> removeUserRole(@PathVariable Long userId,
                                          @PathVariable String roleCode) {
        rbacService.removeUserRole(userId, roleCode);
        return Result.success("角色移除成功");
    }

    @Operation(summary = "查询用户的权限列表")
    @RequirePermission("permission:view")
    @GetMapping("/users/{userId}/permissions")
    public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
        return Result.success(rbacService.getUserPermissions(userId));
    }

    // ────────── 角色-权限关联 ──────────

    @Operation(summary = "查询角色的权限列表")
    @RequirePermission("permission:view")
    @GetMapping("/roles/{roleId}/permissions")
    public Result<List<SysPermission>> getRolePermissions(@PathVariable Long roleId) {
        return Result.success(rbacService.getRolePermissions(roleId));
    }

    @Operation(summary = "为角色分配权限")
    @RequirePermission("permission:manage")
    @PostMapping("/roles/{roleId}/permissions")
    public Result<String> assignRolePermission(@PathVariable Long roleId,
                                                @RequestBody Map<String, Long> body) {
        Long permId = body.get("permId");
        if (permId == null) {
            return Result.error(400, "permId 不能为空");
        }
        rbacService.assignPermissionToRole(roleId, permId);
        return Result.success("权限分配成功");
    }

    @Operation(summary = "移除角色的权限")
    @RequirePermission("permission:manage")
    @DeleteMapping("/roles/{roleId}/permissions/{permId}")
    public Result<String> removeRolePermission(@PathVariable Long roleId,
                                                @PathVariable Long permId) {
        rbacService.removePermissionFromRole(roleId, permId);
        return Result.success("权限移除成功");
    }
}
