package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class ApiServer {
    public static final String JSON_CONTENT_TYPE_HEADER = "Content-Type: application/json" + WebServer.CRLF;

    public static void sendJsonResponse(@NotNull JSONObject response, @NotNull OutputStream output) throws IOException {
        sendJsonResponse(response.toString(), output);
    }

    public static void sendJsonResponse(@NotNull String response, @NotNull OutputStream output) throws IOException {
        byte[] content = response.getBytes();
        output.write((WebServer.RESPONSE_200 + WebServer.getContentLengthHeader(content) + JSON_CONTENT_TYPE_HEADER +
                WebServer.CORS_HEADERS + WebServer.CRLF).getBytes());
        output.write(content);
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }
}