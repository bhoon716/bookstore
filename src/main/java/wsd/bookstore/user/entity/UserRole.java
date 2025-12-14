package wsd.bookstore.user.entity;

public enum UserRole {

    USER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
