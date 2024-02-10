package net.rupyber_studios.police_terminal.webserver.api;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.database.DatabaseSelector;
import net.rupyber_studios.police_terminal.webserver.ApiServer;
import net.rupyber_studios.police_terminal.webserver.Exceptions;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class CitizenApi {
    public static void list(@NotNull String method, String body, OutputStream output) throws IOException {
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
            int page = Exceptions.getInt(request, "page");
            if(!UserApi.isTokenValid(player, token)) throw new Exceptions.UnauthorizedException();
            JSONObject response = new JSONObject();
            response.put("pages", DatabaseSelector.getCitizensPages());
            response.put("citizens", DatabaseSelector.getCitizens(page));
            ApiServer.sendJsonResponse(response, output);
        } catch(Exceptions.HttpException e) {
            e.sendError(output);
        } catch(Exception e) {
            WebServer.send500(output);
            PoliceTerminal.LOGGER.error("Citizen list error: ", e);
        }
    }
}