package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class AuthApi {
    public static String WEB_TOKEN_COOKIE_NAME = "token";

    @Contract("_ -> new")
    public static @NotNull Response validateToken(@NotNull Request request) throws Exceptions.HttpException {
        if(request.requestLine.method != Method.GET) throw new Exceptions.MethodNotAllowedException();
        JSONObject response = new JSONObject();
        response.put("valid", isTokenValid(request.headers.getWebToken()));
        return new Response(Status.OK, response);
    }

    @Contract("_ -> new")
    public static @NotNull Response callsignLoginFeedback(@NotNull Request request) throws Exceptions.HttpException {
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
        return new Response(Status.OK, response);
    }

    public static @NotNull Response login(@NotNull Request request) throws Exceptions.HttpException {
        if(request.requestLine.method != Method.POST) throw new Exceptions.MethodNotAllowedException();
        if(request.body == null) throw new Exceptions.BadRequestException();
        String callsign = Exceptions.getString(request.body, "callsign");
        Integer id = Officer.selectIdWhereCallsign(callsign);
        if(id == null) throw new Exceptions.NotFoundException();
        String password = Exceptions.getString(request.body, "password");
        if(Officer.isPasswordCorrect(id, password)) {
            JSONObject cookie = new JSONObject();
            cookie.put("id", id);
            cookie.put("token", Officer.initToken(id));
            Response response = new Response(Status.NO_CONTENT);
            response.headers.setWebToken(cookie);
            return response;
        }
        else
            throw new Exceptions.UnauthorizedException();
    }

    public static @NotNull Response settings(@NotNull Request request) throws Exceptions.HttpException {
        return switch(request.requestLine.method) {
            case GET -> getSettings(request);
            default -> throw new Exceptions.MethodNotAllowedException();
        };
    }

    @Contract("_ -> new")
    public static @NotNull Response getSettings(@NotNull Request request) throws Exceptions.HttpException {
        WebToken token = request.headers.getWebToken();
        if(!isTokenValid(token)) throw new Exceptions.UnauthorizedException();
        String settings = Player.selectSettings(token.id);
        if(settings == null) throw new Exceptions.UnauthorizedException();
        return new Response(Status.OK, new JSONObject(settings));
    }

    public static boolean isTokenValid(WebToken webToken) {
        try {
            return Officer.isTokenCorrect(webToken.id, webToken.token);
        } catch(Exception e) {
            return false;
        }
    }
}