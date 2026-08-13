package com.example.demo.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.auth.mapper.*;
import com.example.demo.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RbacServiceImplTest {

    private RbacServiceImpl service;
    private SysUserMapper sysUserMapper;
    private SysRoleMapper sysRoleMapper;
    private SysUserRoleMapper sysUserRoleMapper;
    private SysPermissionMapper sysPermissionMapper;
    private SysRolePermissionMapper sysRolePermissionMapper;

    @BeforeEach
    void setUp() {
        sysUserMapper = mock(SysUserMapper.class);
        sysRoleMapper = mock(SysRoleMapper.class);
        sysUserRoleMapper = mock(SysUserRoleMapper.class);
        sysPermissionMapper = mock(SysPermissionMapper.class);
        sysRolePermissionMapper = mock(SysRolePermissionMapper.class);

        service = new RbacServiceImpl(sysUserMapper, sysRoleMapper, sysUserRoleMapper,
                sysPermissionMapper, sysRolePermissionMapper);
    }

    // ────────── createSysUser ──────────

    @Test
    void createSysUser_shouldInsertAndReturn() {
        when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(inv -> {
            SysUser u = inv.getArgument(0);
            u.setId(1L);
            return 1;
        });

        SysUser result = service.createSysUser("testuser", "encodedPwd", "test@example.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getStatus());
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    // ────────── listRoles ──────────

    @Test
    void listRoles_shouldReturnAll() {
        SysRole r1 = new SysRole(); r1.setId(1L); r1.setRoleCode("ADMIN");
        SysRole r2 = new SysRole(); r2.setId(2L); r2.setRoleCode("STUDENT");
        when(sysRoleMapper.selectList(any())).thenReturn(List.of(r1, r2));

        List<SysRole> roles = service.listRoles();
        assertEquals(2, roles.size());
    }

    // ────────── createRole ──────────

    @Test
    void createRole_shouldInsertAndReturn() {
        when(sysRoleMapper.insert(any(SysRole.class))).thenAnswer(inv -> {
            SysRole r = inv.getArgument(0);
            r.setId(10L);
            return 1;
        });

        SysRole result = service.createRole("TEST", "测试角色");
        assertEquals(10L, result.getId());
        assertEquals("TEST", result.getRoleCode());
        assertEquals("测试角色", result.getRoleName());
        verify(sysRoleMapper).insert(any(SysRole.class));
    }

    // ────────── updateRole ──────────

    @Test
    void updateRole_shouldUpdateAndReturn() {
        SysRole existing = new SysRole();
        existing.setId(1L);
        existing.setRoleCode("OLD");
        existing.setRoleName("旧角色");
        when(sysRoleMapper.selectById(1L)).thenReturn(existing);
        when(sysRoleMapper.updateById(any())).thenReturn(1);

        SysRole result = service.updateRole(1L, "NEW", "新角色");
        assertNotNull(result);
        assertEquals("NEW", result.getRoleCode());
        assertEquals("新角色", result.getRoleName());
        verify(sysRoleMapper).updateById(any(SysRole.class));
    }

    @Test
    void updateRole_whenNotFound_shouldReturnNull() {
        when(sysRoleMapper.selectById(999L)).thenReturn(null);
        assertNull(service.updateRole(999L, "X", "Y"));
    }

    // ────────── deleteRole ──────────

    @Test
    void deleteRole_shouldCleanupAndDelete() {
        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("TEST");
        when(sysRoleMapper.selectById(10L)).thenReturn(role);
        when(sysUserRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(sysRolePermissionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(sysRoleMapper.deleteById(10L)).thenReturn(1);

        assertTrue(service.deleteRole(10L));
        verify(sysUserRoleMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysRolePermissionMapper).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMapper).deleteById(10L);
    }

    @Test
    void deleteRole_whenNotFound_shouldReturnFalse() {
        when(sysRoleMapper.selectById(999L)).thenReturn(null);
        assertFalse(service.deleteRole(999L));
    }

    // ────────── listPermissions ──────────

    @Test
    void listPermissions_shouldReturnAll() {
        SysPermission p1 = new SysPermission(); p1.setId(1L); p1.setPermCode("a:view");
        SysPermission p2 = new SysPermission(); p2.setId(2L); p2.setPermCode("b:edit");
        when(sysPermissionMapper.selectList(any())).thenReturn(List.of(p1, p2));

        List<SysPermission> perms = service.listPermissions();
        assertEquals(2, perms.size());
        assertTrue(perms.stream().anyMatch(p -> "a:view".equals(p.getPermCode())));
    }

    // ────────── assignRole ──────────

    @Test
    void assignRole_whenRoleExists_shouldInsertUserRole() {
        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("STUDENT");

        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(role);
        when(sysUserRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sysUserRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

        service.assignRole(1L, "STUDENT");
        verify(sysUserRoleMapper).insert(any(SysUserRole.class));
    }

    @Test
    void assignRole_whenAlreadyAssigned_shouldNotDuplicate() {
        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("STUDENT");

        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(role);
        when(sysUserRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        service.assignRole(1L, "STUDENT");
        verify(sysUserRoleMapper, never()).insert(any(SysUserRole.class));
    }

    // ────────── removeUserRole ──────────

    @Test
    void removeUserRole_shouldDeleteAssociation() {
        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("STUDENT");
        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(role);
        when(sysUserRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        service.removeUserRole(1L, "STUDENT");
        verify(sysUserRoleMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void removeUserRole_whenRoleNotFound_shouldDoNothing() {
        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        service.removeUserRole(1L, "NONEXISTENT");
        verify(sysUserRoleMapper, never()).delete(any());
    }

    // ────────── getUserRoles ──────────

    @Test
    void getUserRoles_shouldReturnRoles() {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(1L);
        ur.setRoleId(10L);

        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("STUDENT");
        role.setRoleName("学生");

        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(ur));
        when(sysRoleMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(role));

        List<SysRole> roles = service.getUserRoles(1L);
        assertEquals(1, roles.size());
        assertEquals("STUDENT", roles.get(0).getRoleCode());
    }

    @Test
    void getUserRoles_whenNoRoles_shouldReturnEmpty() {
        when(sysUserRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        assertTrue(service.getUserRoles(1L).isEmpty());
    }

    // ────────── getUserPermissions ──────────

    @Test
    void getUserPermissions_shouldReturnPermCodes() {
        when(sysPermissionMapper.selectPermCodesByUserId(1L))
                .thenReturn(List.of("student:view", "course:view"));

        List<String> perms = service.getUserPermissions(1L);
        assertEquals(2, perms.size());
        assertTrue(perms.contains("student:view"));
    }

    // ────────── assignPermissionToRole ──────────

    @Test
    void assignPermissionToRole_shouldInsert() {
        when(sysRolePermissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sysRolePermissionMapper.insert(any(SysRolePermission.class))).thenReturn(1);

        service.assignPermissionToRole(10L, 20L);
        verify(sysRolePermissionMapper).insert(any(SysRolePermission.class));
    }

    @Test
    void assignPermissionToRole_whenAlreadyExists_shouldNotDuplicate() {
        when(sysRolePermissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        service.assignPermissionToRole(10L, 20L);
        verify(sysRolePermissionMapper, never()).insert(any());
    }

    // ────────── removePermissionFromRole ──────────

    @Test
    void removePermissionFromRole_shouldDelete() {
        when(sysRolePermissionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        service.removePermissionFromRole(10L, 20L);
        verify(sysRolePermissionMapper).delete(any(LambdaQueryWrapper.class));
    }

    // ────────── getRolePermissions ──────────

    @Test
    void getRolePermissions_shouldReturnPermissions() {
        SysRolePermission rp = new SysRolePermission();
        rp.setRoleId(10L);
        rp.setPermissionId(1L);

        SysPermission perm = new SysPermission();
        perm.setId(1L);
        perm.setPermCode("student:view");

        when(sysRolePermissionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(rp));
        when(sysPermissionMapper.selectBatchIds(List.of(1L))).thenReturn(List.of(perm));

        List<SysPermission> perms = service.getRolePermissions(10L);
        assertEquals(1, perms.size());
        assertEquals("student:view", perms.get(0).getPermCode());
    }

    @Test
    void getRolePermissions_whenEmpty_shouldReturnEmpty() {
        when(sysRolePermissionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        assertTrue(service.getRolePermissions(10L).isEmpty());
    }

    // ────────── getRoleByCode ──────────

    @Test
    void getRoleByCode_shouldReturnRole() {
        SysRole role = new SysRole();
        role.setId(10L);
        role.setRoleCode("TEACHER");
        role.setRoleName("任课教师");
        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(role);

        SysRole result = service.getRoleByCode("TEACHER");
        assertNotNull(result);
        assertEquals("TEACHER", result.getRoleCode());
    }

    @Test
    void getRoleByCode_whenNotFound_shouldReturnNull() {
        when(sysRoleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertNull(service.getRoleByCode("NONEXISTENT"));
    }
}
