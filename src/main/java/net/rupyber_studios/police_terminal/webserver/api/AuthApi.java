package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Objects;

public class AuthApi {
    public static void validateToken(Request request, OutputStream output) throws IOException {
        try {
            if(request.requestLine.method != Method.GET) throw new Exceptions.MethodNotAllowedException();
            JSONObject response = new JSONObject();
            response.put("valid", isTokenValid(request.headers.getWebToken()));
            ApiServer.sendJsonResponse(response, output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Validate token error: ", e);
        }
    }

    public static void callsignLoginFeedback(Request request, OutputStream output) throws IOException {
        try {
            if(request.requestLine.method != Method.GET) throw new Exceptions.MethodNotAllowedException();
            String callsign = request.requestLine.uri.getParameter("callsign");
            if(callsign == null) throw new Exceptions.BadRequestException();
            JSONObject response = new JSONObject();
            String feedback;
            if(Callsign.isInUse(callsign))
                feedback = "Valid Callsign";
            else
                feedback = "Callsign does not exist!";
            response.put("feedback", feedback);
            ApiServer.sendJsonResponse(response, output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Callsign login feedback error: ", e);
        }
    }

    public static void login(Request request, OutputStream output) throws IOException {
        try {
            if(request.requestLine.method != Method.POST) throw new Exceptions.MethodNotAllowedException();
            if(request.body == null) throw new Exceptions.BadRequestException();
            JSONObject requestBody = request.getJsonBody();
            String callsign = Exceptions.getString(requestBody, "callsign");
            Integer id = Officer.selectIdWhereCallsign(callsign);
            if(id == null) throw new Exceptions.NotFoundException();
            String password = Exceptions.getString(requestBody, "password");
            if(Officer.isPasswordCorrect(id, password)) {
                JSONObject cookie = new JSONObject();
                cookie.put("id", id);
                cookie.put("token", Officer.initToken(id));
                ApiServer.sendSetCookieResponse("token", cookie.toString(), output);
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

    public static void settings(@NotNull Request request, OutputStream output) throws IOException {
        try {
            switch(request.requestLine.method) {
                case GET -> getSettings(request, output);
                default -> WebServer.send405(output);
            }
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Get settings error: ", e);
        }
    }

    public static void getSettings(@NotNull Request request, OutputStream output)
            throws IOException, Exceptions.HttpException, SQLException {
        WebToken token = request.headers.getWebToken();
        if(!isTokenValid(token)) throw new Exceptions.UnauthorizedException();
        ApiServer.sendResponse(Objects.requireNonNull(Player.selectSettings(token.id)), output);
    }

    public static boolean isTokenValid(WebToken webToken) {
        try {
            return Officer.isTokenCorrect(webToken.id, webToken.token);
        } catch(Exception e) {
            return false;
        }
    }
}