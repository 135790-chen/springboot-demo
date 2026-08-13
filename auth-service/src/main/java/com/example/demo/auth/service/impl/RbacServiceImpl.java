package com.example.demo.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.auth.mapper.*;
import com.example.demo.auth.service.RbacService;
import com.example.demo.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class RbacServiceImpl implements RbacService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    public RbacServiceImpl(SysUserMapper sysUserMapper,
                           SysRoleMapper sysRoleMapper,
                           SysUserRoleMapper sysUserRoleMapper,
                           SysPermissionMapper sysPermissionMapper,
                           SysRolePermissionMapper sysRolePermissionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
    }

    // ────────── 用户管理 ──────────

    @Override
    @Transactional
    public SysUser createSysUser(String username, String password, String email) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(password);
        sysUser.setEmail(email);
        sysUser.setStatus(1);
        sysUser.setGmtCreate(LocalDateTime.now());
        sysUser.setGmtModified(LocalDateTime.now());
        sysUserMapper.insert(sysUser);
        return sysUser;
    }

    // ────────── 角色 CRUD ──────────

    @Override
    public List<SysRole> listRoles() {
        return sysRoleMapper.selectList(null);
    }

    @Override
    public SysRole createRole(String roleCode, String roleName) {
        SysRole role = new SysRole();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setGmtCreate(LocalDateTime.now());
        sysRoleMapper.insert(role);
        return role;
    }

    @Override
    public SysRole updateRole(Long roleId, String roleCode, String roleName) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            return null;
        }
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        sysRoleMapper.updateById(role);
        return role;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            return false;
        }
        // 清理用户-角色关联
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getRoleId, roleId);
        sysUserRoleMapper.delete(urWrapper);
        // 清理角色-权限关联
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, roleId);
        sysRolePermissionMapper.delete(rpWrapper);
        // 删除角色
        sysRoleMapper.deleteById(roleId);
        return true;
    }

    @Override
    public SysRole getRoleByCode(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        return sysRoleMapper.selectOne(wrapper);
    }

    // ────────── 权限查询 ──────────

    @Override
    public List<SysPermission> listPermissions() {
        return sysPermissionMapper.selectList(null);
    }

    // ────────── 用户-角色关联 ──────────

    @Override
    @Transactional
    public void assignRole(Long userId, String roleCode) {
        SysRole role = getRoleByCode(roleCode);
        if (role == null) {
            return;
        }
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId)
               .eq(SysUserRole::getRoleId, role.getId());
        if (sysUserRoleMapper.selectCount(wrapper) == 0) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            sysUserRoleMapper.insert(userRole);
        }
    }

    @Override
    public void removeUserRole(Long userId, String roleCode) {
        SysRole role = getRoleByCode(roleCode);
        if (role == null) {
            return;
        }
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId)
               .eq(SysUserRole::getRoleId, role.getId());
        sysUserRoleMapper.delete(wrapper);
    }

    @Override
    public List<SysRole> getUserRoles(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(wrapper);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
        return sysRoleMapper.selectBatchIds(roleIds);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return sysPermissionMapper.selectPermCodesByUserId(userId);
    }

    // ────────── 角色-权限关联 ──────────

    @Override
    @Transactional
    public void assignPermissionToRole(Long roleId, Long permId) {
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId)
               .eq(SysRolePermission::getPermissionId, permId);
        if (sysRolePermissionMapper.selectCount(wrapper) == 0) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            sysRolePermissionMapper.insert(rp);
        }
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permId) {
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId)
               .eq(SysRolePermission::getPermissionId, permId);
        sysRolePermissionMapper.delete(wrapper);
    }

    @Override
    public List<SysPermission> getRolePermissions(Long roleId) {
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rps = sysRolePermissionMapper.selectList(wrapper);
        if (rps.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permIds = rps.stream().map(SysRolePermission::getPermissionId).toList();
        return sysPermissionMapper.selectBatchIds(permIds);
    }
}
