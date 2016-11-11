package com.livingprogress.mentorme.security;

import com.livingprogress.mentorme.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * The custom user details.
 */
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    /**
     * The user.
     */
    @Getter
    private User user;

    /**
     * Custom user details constructor.
     * @param entity the user
     * @param username the username.
     * @param password the password
     * @param authorities the authorities
     */
    public CustomUserDetails(User entity, String username,
            String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.user = entity;
    }

    /**
     * Custom user details constructor.
     * @param entity the user
     * @param enabled the enabled flag.
     * @param accountNonExpired the account non expired flag
     * @param credentialsNonExpired the credentials non expired flag.
     * @param accountNonLocked the account non locked flag.
     * @param authorities the authorities
     */
    public CustomUserDetails(User entity, boolean enabled, boolean accountNonExpired, boolean
            credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(entity.getUsername(), entity.getPassword(), enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = entity;
    }
}
