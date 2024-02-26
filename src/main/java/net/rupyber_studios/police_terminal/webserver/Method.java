package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Method {
    GET, POST, PUT, PATCH, DELETE;

    @Contract(pure = true)
    public static Method fromString(@NotNull String method) throws Exceptions.BadRequestException {
        return switch(method) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "PUT" -> PUT;
            case "PATCH" -> PATCH;
            case "DELETE" -> DELETE;
            default -> throw new Exceptions.BadRequestException();
        };
    }
}