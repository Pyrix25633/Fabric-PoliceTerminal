package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.webserver.api.CitizenApi;
import net.rupyber_studios.police_terminal.webserver.api.OfficerApi;
import net.rupyber_studios.police_terminal.webserver.api.UserApi;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

public class ApiServer {
    public static final String JSON_CONTENT_TYPE_HEADER = "Content-Type: application/json" + WebServer.CRLF;

    public static void serveApi(@NotNull String path, RequestLine requestLine, HashMap<String, String> headers,
                                InputStream input, OutputStream output) throws IOException, URISyntaxException {
        switch(path) {
            case "/user/validate-token" -> UserApi.validateToken(requestLine.method(),
                    WebServer.parseBody(input, headers), output);
            case "/user/callsign-login-feedback" -> UserApi.callsignLoginFeedback(requestLine.method(),
                    WebServer.parseQueryString(requestLine.uri()), output);
            case "/user/login" -> UserApi.login(requestLine.method(), WebServer.parseBody(input, headers), output);
            case "/user/get-settings" -> UserApi.getSettings(requestLine.method(),
                    WebServer.parseBody(input, headers), output);
            case "/citizen/list" -> CitizenApi.list(requestLine.method(), WebServer.parseBody(input, headers), output);
            case "/officer/list" -> OfficerApi.list(requestLine.method(), WebServer.parseBody(input, headers), output);
            default -> FileServer.serveFile("GET", "/404", output);
        }
    }

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