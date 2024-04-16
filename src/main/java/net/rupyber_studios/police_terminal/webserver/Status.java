package net.rupyber_studios.police_terminal.webserver;

public enum Status {
    OK("200 OK"),
    CREATED("201 Created"),
    NO_CONTENT("204 No Content"),

    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    UNPROCESSABLE_CONTENT("422 Unprocessable Content"),

    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    public final String code;

    Status(String code) {
        this.code = code;
    }
}