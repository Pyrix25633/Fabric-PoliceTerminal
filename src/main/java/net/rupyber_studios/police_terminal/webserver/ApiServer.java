package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ApiServer {
    public static final String JSON_CONTENT_TYPE_HEADER = "Content-Type: application/json" + WebServer.CRLF;

    public static void validateToken(@NotNull String method, String body, OutputStream output) throws IOException {
        if(!method.equals("POST")) {
            WebServer.send405(output);
            return;
        }
        if(body == null) {
            WebServer.send400(output);
            return;
        }
        try {
            JSONObject request = new JSONObject(body);
            UUID player = UUID.fromString(request.getString("uuid"));
            String token = request.getString("token");
            JSONObject response = new JSONObject();
            response.put("valid", DatabaseManager.isPlayerTokenCorrect(player, token));
            sendJsonResponse(response, output);
        } catch(Exception e) {
            WebServer.send400(output);
        }
    }

    public static void sendJsonResponse(@NotNull JSONObject response, @NotNull OutputStream output) throws IOException {
        byte[] content = response.toString().getBytes();
        output.write((WebServer.RESPONSE_200 + WebServer.getContentLengthHeader(content) + JSON_CONTENT_TYPE_HEADER +
                WebServer.CRLF).getBytes());
        output.write(content);
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }
}