package id.go.kemenkoinfra.ipfo.sifpi.common.constants;

public final class AuthPath {

    private AuthPath() {} // prevent instantiation

    public static final String API = "/api";
    public static final String BASE = "/auth";

    public static final String REGISTER = BASE + "/register";
    public static final String LOGIN = BASE + "/login";
    public static final String LOGOUT = BASE + "/logout";
    public static final String SET_PASSWORD = BASE + "/set-password";

    public static final String ROLES = "/roles";

    public static final String[] PUBLIC = {
        API + REGISTER + "/**",
        API + LOGIN,
        API + LOGOUT,
        API + SET_PASSWORD
    };

}