package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.webserver.ApiServer;
import net.rupyber_studios.police_terminal.webserver.Exceptions;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserApi {
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
            UUID player = UUID.fromString(Exceptions.getString(request, "uuid"));
            String token = Exceptions.getString(request, "token");
            JSONObject response = new JSONObject();
            response.put("valid", DatabaseManager.isPlayerTokenCorrect(player, token));
            ApiServer.sendJsonResponse(response, output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Validate token error: ", e);
        }
    }

    public static void callsignLoginFeedback(@NotNull String method, @NotNull HashMap<String, String> parameters,
                                             OutputStream output) throws IOException {
        if(!method.equals("GET")) {
            WebServer.send405(output);
            return;
        }
        String callsign = parameters.get("callsign");
        if(callsign == null) {
            WebServer.send400(output);
            return;
        }
        try {
            JSONObject response = new JSONObject();
            String feedback;
            if(DatabaseManager.isCallsignInUse(callsign))
                feedback = "Valid Callsign";
            else
                feedback = "Callsign does not exist!";
            response.put("feedback", feedback);
            ApiServer.sendJsonResponse(response, output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Callsign login feedback error: ", e);
        }
    }

    public static void login(@NotNull String method, String body, OutputStream output) throws IOException {
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
            String callsign = Exceptions.getString(request, "callsign");
            UUID player = DatabaseManager.getPlayerUuidFromCallsign(callsign);
            if(player == null) throw new Exceptions.NotFoundException();
            String password = Exceptions.getString(request, "password");
            if(DatabaseManager.isPlayerPasswordCorrect(player, password)) {
                JSONObject response = new JSONObject();
                response.put("uuid", player.toString());
                response.put("token", DatabaseManager.initPlayerToken(player));
                ApiServer.sendJsonResponse(response, output);
            }
            else
                throw new Exceptions.UnauthorizedException();
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Login error: ", e);
        }
    }

    public static void getSettings(@NotNull String method, String body, OutputStream output) throws IOException {
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
            UUID player = UUID.fromString(Exceptions.getString(request, "uuid"));
            String token = Exceptions.getString(request, "token");
            if(!isTokenValid(player, token)) throw new Exceptions.UnauthorizedException();
            ApiServer.sendJsonResponse(Objects.requireNonNull(DatabaseManager.getPlayerSettings(player)), output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Login error: ", e);
        }
    }

    public static boolean isTokenValid(UUID player, String token) {
        try {
            return DatabaseManager.isPlayerTokenCorrect(player, token);
        } catch(Exception e) {
            return false;
        }
    }
}