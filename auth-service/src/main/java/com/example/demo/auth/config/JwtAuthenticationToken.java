package com.example.demo.auth.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * JWT 认证令牌 — 包装从 JWT 中提取的用户信息和权限。
 * <p>
 * 权限码（如 "course:manage"）被转换为 {@link SimpleGrantedAuthority}，
 * 配合 {@code @PreAuthorize("hasAuthority('course:manage')")} 使用。
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final String username;

    /**
     * 构造已认证的 Token（JWT 验证通过后调用）
     */
    public JwtAuthenticationToken(Long userId, String username,
                                   List<String> permissions) {
        super(toAuthorities(permissions));
        this.userId = userId;
        this.username = username;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> toAuthorities(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
