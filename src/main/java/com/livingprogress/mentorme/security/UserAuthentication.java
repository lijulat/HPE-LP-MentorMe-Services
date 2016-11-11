package com.livingprogress.mentorme.security;

import com.livingprogress.mentorme.entities.User;
import com.livingprogress.mentorme.entities.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The user authentication.
 */
public class UserAuthentication implements Authentication {
    /**
     * The user.
     */
    private User user;

    /**
     * The authenticated flag.
     */
    private boolean authenticated = true;

    /**
     * The user authentication constructor.
     *
     * @param entity the user.
     */
    public UserAuthentication(User entity) {
        this.user = entity;
    }

    /**
     * Get authorities.
     *
     * @return the user  authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> auths = new HashSet<>();
        List<UserRole> roles = user.getRoles();
        if (roles != null) {
            for (UserRole role : roles) {
                auths.add(new SimpleGrantedAuthority(role.getValue()));
            }
        }
        return new ArrayList<>(auths);
    }

    /**
     * Get credentials.
     *
     * @return the credentials
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Get user details.
     *
     * @return the user details.
     */
    @Override
    public Object getDetails() {
        return user;
    }

    /**
     * Get user principal.
     *
     * @return the user principal
     */
    @Override
    public Object getPrincipal() {
        return user;
    }

    /**
     * Return authenticated flag.
     *
     * @return authenticated flag.
     */
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Set authenticated flag.
     *
     * @param authenticatedFlag authenticated flag
     * @throws IllegalArgumentException throws if invalid request.
     */
    @Override
    public void setAuthenticated(boolean authenticatedFlag) throws IllegalArgumentException {
        this.authenticated = authenticatedFlag;
    }

    /**
     * Get user name.
     *
     * @return the username.
     */
    @Override
    public String getName() {
        return user.getUsername();
    }
}
