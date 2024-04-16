package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import static net.rupyber_studios.rupyber_database_api.jooq.Tables.EmergencyCalls;

public class EmergencyCallApi {
    private static final List<String> COLUMNS = List.of(
            EmergencyCalls.callNumber.getName(),
            EmergencyCalls.locationX.getName(),
            EmergencyCalls.locationY.getName(),
            EmergencyCalls.locationZ.getName(),
            EmergencyCalls.createdAt.getName(),
            "caller",
            "responder",
            EmergencyCalls.closedAt.getName()
    );

    public static @NotNull Response emergencyCalls(@NotNull Request request) throws Exceptions.HttpException {
        return switch(request.requestLine.method) {
            case GET -> getEmergencyCalls(request);
            default -> throw new Exceptions.MethodNotAllowedException();
        };
    }

    @Contract("_ -> new")
    public static @NotNull Response getEmergencyCalls(@NotNull Request request) throws Exceptions.HttpException {
        int page = Exceptions.getInt(request, "page");
        String orderColumn = Exceptions.getString(request, "order[column]");
        if(!COLUMNS.contains(orderColumn)) throw new Exceptions.BadRequestException();
        boolean orderAscending = Exceptions.getBoolean(request, "order[ascending]");
        if(!AuthApi.isTokenValid(request.headers.getWebToken())) throw new Exceptions.UnauthorizedException();
        JSONObject response = new JSONObject();
        response.put("pages", EmergencyCall.selectNumberOfEmergencyCallPages());
        response.put("emergencyCalls", EmergencyCall.selectEmergencyCalls(page, orderColumn, orderAscending));
        return new Response(Status.OK, response);
    }
}