package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.InheritanceType.JOINED;

/**
 * The user.
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = JOINED)
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
    @ManyToMany(fetch = EAGER)
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

    /**
     * The is virtual user flag.
     */
    private boolean isVirtualUser;

    /**
     * The street address.
     */
    private String streetAddress;

    /**
     * The city.
     */
    private String city;

    /**
     * The state.
     */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "state_id")
    private State state;

    /**
     * The country.
     */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * The postal code.
     */
    private String postalCode;

    /**
     * The longitude.
     */
    @Max(180)
    @Min(-180)
    private BigDecimal longitude;

    /**
     * The latitude.
     */
    @Max(90)
    @Min(-90)
    private BigDecimal latitude;
}

