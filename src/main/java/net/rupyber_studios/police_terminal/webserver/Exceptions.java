package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class Exceptions {
    public static JSONObject getJSONObject(@NotNull JSONObject json, String key) throws BadRequestException {
        try {
            return json.getJSONObject(key);
        } catch(JSONException e) {
            throw new BadRequestException();
        }
    }

    public static String getString(@NotNull JSONObject json, String key) throws BadRequestException {
        try {
            return json.getString(key);
        } catch(JSONException e) {
            throw new BadRequestException();
        }
    }

    public static @NotNull String getString(@NotNull Request request, String parameter) throws BadRequestException {
        String value = request.requestLine.uri.getParameter(parameter);
        if(value == null || value.isEmpty()) throw new BadRequestException();
        return value;
    }

    public static int getInt(@NotNull JSONObject json, String key) throws BadRequestException {
        try {
            return json.getInt(key);
        } catch(JSONException e) {
            throw new BadRequestException();
        }
    }

    public static int getInt(@NotNull Request request, String parameter) throws BadRequestException {
        String value = getString(request, parameter);
        return Integer.parseInt(value);
    }

    public static boolean getBoolean(@NotNull JSONObject json, String key) throws BadRequestException {
        try {
            return json.getBoolean(key);
        } catch(JSONException e) {
            throw new BadRequestException();
        }
    }

    public static boolean getBoolean(@NotNull Request request, String parameter) throws BadRequestException {
        String value = getString(request, parameter);
        return Boolean.parseBoolean(value);
    }

    public static abstract class HttpException extends Exception {
        public abstract void sendError(OutputStream output) throws IOException;
    }

    public static class BadRequestException extends HttpException {
        @Override
        public void sendError(OutputStream output) throws IOException {
            WebServer.send400(output);
        }
    }

    public static class UnauthorizedException extends HttpException {
        @Override
        public void sendError(OutputStream output) throws IOException {
            WebServer.send401(output);
        }
    }

    public static class NotFoundException extends HttpException {
        @Override
        public void sendError(OutputStream output) throws IOException {
            WebServer.send404(output);
        }
    }

    public static class MethodNotAllowedException extends HttpException {
        @Override
        public void sendError(OutputStream output) throws IOException {
            WebServer.send405(output);
        }
    }
}