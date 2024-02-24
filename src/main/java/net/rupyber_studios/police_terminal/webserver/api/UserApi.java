package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.webserver.ApiServer;
import net.rupyber_studios.police_terminal.webserver.Exceptions;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

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
            int id = Exceptions.getInt(request, "id");
            String token = Exceptions.getString(request, "token");
            JSONObject response = new JSONObject();
            response.put("valid", Player.isTokenCorrect(id, token));
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
            if(Callsign.isInUse(callsign))
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
            int id = Player.selectIdFromCallsign(callsign);
            if(id == 0) throw new Exceptions.NotFoundException();
            String password = Exceptions.getString(request, "password");
            if(Player.isPasswordCorrect(id, password)) {
                JSONObject response = new JSONObject();
                response.put("id", id);
                response.put("token", Player.initToken(id));
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
            int id = Exceptions.getInt(request, "id");
            String token = Exceptions.getString(request, "token");
            if(!isTokenValid(id, token)) throw new Exceptions.UnauthorizedException();
            ApiServer.sendJsonResponse(Objects.requireNonNull(Player.selectSettings(id)), output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Get settings error: ", e);
        }
    }

    public static boolean isTokenValid(int id, String token) {
        try {
            return Player.isTokenCorrect(id, token);
        } catch(Exception e) {
            return false;
        }
    }
}