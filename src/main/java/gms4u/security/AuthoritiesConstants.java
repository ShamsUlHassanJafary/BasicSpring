package gms4u.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String GARAGE_OWNER = "ROLE_GARAGE_OWNER";

    public static final String GARAGE_ADMIN = "ROLE_GARAGE_ADMIN";

    public static final String CUSTOMER = "ROLE_CUSTOMER";

    private AuthoritiesConstants() {
    }
}
