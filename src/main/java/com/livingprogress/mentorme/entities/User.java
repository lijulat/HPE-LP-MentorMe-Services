package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.EnumType.STRING;

/**
 * The user.
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AuditableEntity {
    /**
     * An username.
     */
    private String username;

    /**
     * The password (hashed).
     */
    @JsonProperty(access = WRITE_ONLY)
    private String password;

    /**
     * The first name.
     */
    private String firstName;

    /**
     * The last name.
     */
    private String lastName;

    /**
     * The user roles.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_role_id")})
    private List<UserRole> roles;

    /**
     * The email.
     */
    private String email;

    /**
     * The profile picture path.
     */
    private String profilePicturePath;

    /**
     * The provider id.
     */
    @JsonIgnore
    private String providerId;

    /**
     * The provider user id.
     */
    @JsonIgnore
    private String providerUserId;

    /**
     * The access token.
     */
    @JsonIgnore
    private String accessToken;

    /**
     * Expires in millis.
     */
    @Transient
    @JsonInclude(NON_NULL)
    private Long expires;

    /**
     * The user status.
     */
    @Enumerated(STRING)
    private UserStatus status;
}

