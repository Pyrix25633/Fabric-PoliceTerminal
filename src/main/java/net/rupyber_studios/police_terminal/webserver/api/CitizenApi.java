package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.util.Citizen;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class CitizenApi {
    private static final List<String> ORDER_FIELDS = List.of("uuid", "username", "online");

    public static void citizens(@NotNull Request request, OutputStream output) throws IOException {
        try {
            switch(request.requestLine.method) {
                case GET -> getCitizens(request, output);
                default -> throw new Exceptions.MethodNotAllowedException();
            }
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Citizen list error: ", e);
        }
    }

    public static void getCitizens(@NotNull Request request, OutputStream output)
            throws IOException, Exceptions.HttpException, SQLException {
        int page = Exceptions.getInt(request, "page");
        String orderField = Exceptions.getString(request, "order[field]");
        if(!ORDER_FIELDS.contains(orderField)) throw new Exceptions.BadRequestException();
        boolean orderAscending = Exceptions.getBoolean(request, "order[ascending]");
        if(!AuthApi.isTokenValid(request.headers.getWebToken())) throw new Exceptions.UnauthorizedException();
        JSONObject response = new JSONObject();
        response.put("pages", Citizen.selectNumberOfCitizenPages());
        response.put("citizens", Citizen.selectCitizens(page, orderField, orderAscending));
        ApiServer.sendJsonResponse(response, output);
    }
}