package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.webserver.api.CitizenApi;
import net.rupyber_studios.police_terminal.webserver.api.EmergencyCallApi;
import net.rupyber_studios.police_terminal.webserver.api.OfficerApi;
import net.rupyber_studios.police_terminal.webserver.api.AuthApi;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class ApiServer {
    public static final String JSON_CONTENT_TYPE_HEADER = "Content-Type: application/json" + WebServer.CRLF;

    public static void serveApi(@NotNull Request request, OutputStream output)
            throws IOException {
        switch(request.requestLine.uri.cleanApiUri) {
            case "/auth/validate-token" -> AuthApi.validateToken(request, output);
            case "/auth/callsign-login-feedback" -> AuthApi.callsignLoginFeedback(request, output);
            case "/auth/login" -> AuthApi.login(request, output);
            case "/auth/settings" -> AuthApi.settings(request, output);
            case "/citizens" -> CitizenApi.citizens(request, output);
            case "/officers" -> OfficerApi.officers(request, output);
            case "/emergency-calls" -> EmergencyCallApi.emergencyCalls(request, output);
            default -> FileServer.serveFile(request, "/404", output);
        }
    }

    public static void sendJsonResponse(@NotNull JSONObject response, @NotNull OutputStream output) throws IOException {
        sendResponse(response.toString(), output);
    }

    public static void sendEmptyResponse(@NotNull OutputStream output) throws IOException {
        output.write((WebServer.RESPONSE_204 + WebServer.getContentLengthHeader(new byte[0]) +
                WebServer.CORS_HEADERS + WebServer.CRLF).getBytes());
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }

    public static void sendSetCookieResponse(String name, String value, @NotNull OutputStream output) throws IOException {
        output.write((WebServer.RESPONSE_204 + WebServer.getContentLengthHeader(new byte[0]) +
                WebServer.CORS_HEADERS + WebServer.SET_COOKIE_HEADER + name + "=" + value +
                "; Path=/; SameSite=Strict; HttpOnly").getBytes());
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }

    public static void sendResponse(@NotNull String response, @NotNull OutputStream output) throws IOException {
        byte[] content = response.getBytes();
        output.write((WebServer.RESPONSE_200 + WebServer.getContentLengthHeader(content) + JSON_CONTENT_TYPE_HEADER +
                WebServer.CORS_HEADERS + WebServer.CRLF).getBytes());
        output.write(content);
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }
}