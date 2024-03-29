package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class OfficerApi {
    private static final List<String> ORDER_FIELDS = List.of("uuid", "username", "online", "status",
            "rankId", "callsign", "callsignReserved");

    public static void officers(Request request, OutputStream output) throws IOException {
        try {
            switch(request.requestLine.method) {
                case GET -> getOfficers(request, output);
                default -> throw new Exceptions.MethodNotAllowedException();
            }
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Officer list error: ", e);
        }
    }

    public static void getOfficers(@NotNull Request request, OutputStream output)
            throws IOException, Exceptions.HttpException, SQLException {
        int page = Exceptions.getInt(request, "page");
        String orderField = Exceptions.getString(request, "order[field]");
        if(!ORDER_FIELDS.contains(orderField)) throw new Exceptions.BadRequestException();
        boolean orderAscending = Exceptions.getBoolean(request, "order[ascending]");
        if(!AuthApi.isTokenValid(request.headers.getWebToken())) throw new Exceptions.UnauthorizedException();
        JSONObject response = new JSONObject();
        response.put("pages", Officer.selectNumberOfOfficerPages());
        response.put("officers", Officer.selectOfficers(page, orderField, orderAscending));
        ApiServer.sendJsonResponse(response, output);
    }
}