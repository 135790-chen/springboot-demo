package com.example.demo.auth.service;

import com.example.demo.entity.SysPermission;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;

import java.util.List;

/**
 * RBAC 权限服务接口
 */
public interface RbacService {

    // ────────── 用户管理 ──────────

    /** 创建系统用户（注册时调用） */
    SysUser createSysUser(String username, String password, String email);

    // ────────── 角色 CRUD ──────────

    /** 列出所有角色 */
    List<SysRole> listRoles();

    /** 创建角色 */
    SysRole createRole(String roleCode, String roleName);

    /** 更新角色 */
    SysRole updateRole(Long roleId, String roleCode, String roleName);

    /** 删除角色（同时清理关联数据） */
    boolean deleteRole(Long roleId);

    /** 根据 roleCode 查询角色 */
    SysRole getRoleByCode(String roleCode);

    // ────────── 权限查询 ──────────

    /** 列出所有权限 */
    List<SysPermission> listPermissions();

    // ────────── 用户-角色关联 ──────────

    /** 为用户分配角色（通过 roleCode） */
    void assignRole(Long userId, String roleCode);

    /** 移除用户的某个角色 */
    void removeUserRole(Long userId, String roleCode);

    /** 查询用户的所有角色 */
    List<SysRole> getUserRoles(Long userId);

    /** 查询用户的所有权限编码 */
    List<String> getUserPermissions(Long userId);

    // ────────── 角色-权限关联 ──────────

    /** 为角色分配权限 */
    void assignPermissionToRole(Long roleId, Long permId);

    /** 移除角色的某个权限 */
    void removePermissionFromRole(Long roleId, Long permId);

    /** 查询角色的所有权限 */
    List<SysPermission> getRolePermissions(Long roleId);
}
