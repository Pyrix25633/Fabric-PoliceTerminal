package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.webserver.*;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import static net.rupyber_studios.rupyber_database_api.jooq.Tables.Players;

public class OfficerApi {
    private static final List<String> COLUMNS = List.of(
            Players.uuid.getName(),
            Players.username.getName(),
            Players.online.getName(),
            "status",
            "rank",
            Players.callsign.getName(),
            Players.callsignReserved.getName()
    );

    public static @NotNull Response officers(@NotNull Request request) throws Exceptions.HttpException {
        return switch(request.requestLine.method) {
            case GET -> getOfficers(request);
            default -> throw new Exceptions.MethodNotAllowedException();
        };
    }

    @Contract("_ -> new")
    public static @NotNull Response getOfficers(@NotNull Request request) throws Exceptions.HttpException {
        int page = Exceptions.getInt(request, "page");
        String orderColumn = Exceptions.getString(request, "order[column]");
        if(!COLUMNS.contains(orderColumn)) throw new Exceptions.BadRequestException();
        if(orderColumn.equals("rank") || orderColumn.equals("status"))
            orderColumn += "Id";
        boolean orderAscending = Exceptions.getBoolean(request, "order[ascending]");
        if(!AuthApi.isTokenValid(request.headers.getWebToken())) throw new Exceptions.UnauthorizedException();
        JSONObject response = new JSONObject();
        response.put("pages", Officer.selectNumberOfOfficerPages());
        response.put("officers", Officer.selectOfficers(page, orderColumn, orderAscending));
        return new Response(Status.OK, response);
    }
}