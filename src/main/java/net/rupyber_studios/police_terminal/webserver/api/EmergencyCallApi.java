package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.webserver.ApiServer;
import net.rupyber_studios.police_terminal.webserver.Exceptions;
import net.rupyber_studios.police_terminal.webserver.Request;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class EmergencyCallApi {
    private static final List<String> ORDER_FIELDS = List.of("callNumber", "locationX", "locationY", "locationZ",
            "createdAt", "caller", "closed");

    public static void emergencyCalls(@NotNull Request request, OutputStream output) throws IOException {
        try {
            switch(request.requestLine.method) {
                case GET -> getEmergencyCalls(request, output);
                default -> throw new Exceptions.MethodNotAllowedException();
            }
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Citizen list error: ", e);
        }
    }

    public static void getEmergencyCalls(@NotNull Request request, OutputStream output)
            throws IOException, Exceptions.HttpException, SQLException {
        int page = Exceptions.getInt(request, "page");
        String orderColumn = Exceptions.getString(request, "order[column]");
        if(!ORDER_FIELDS.contains(orderColumn)) throw new Exceptions.BadRequestException();
        boolean orderAscending = Exceptions.getBoolean(request, "order[ascending]");
        if(!AuthApi.isTokenValid(request.headers.getWebToken())) throw new Exceptions.UnauthorizedException();
        JSONObject response = new JSONObject();
        response.put("pages", EmergencyCall.selectNumberOfEmergencyCallPages());
        response.put("emergencyCalls", EmergencyCall.selectEmergencyCalls(page, orderColumn, orderAscending));
        ApiServer.sendJsonResponse(response, output);
    }
}