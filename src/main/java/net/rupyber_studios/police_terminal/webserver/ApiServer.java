package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.webserver.api.CitizenApi;
import net.rupyber_studios.police_terminal.webserver.api.EmergencyCallApi;
import net.rupyber_studios.police_terminal.webserver.api.OfficerApi;
import net.rupyber_studios.police_terminal.webserver.api.AuthApi;
import org.jetbrains.annotations.NotNull;


public class ApiServer {
    public static Response serveApi(@NotNull Request request)
            throws Exceptions.HttpException {
        return switch(request.requestLine.uri.cleanApiUri) {
            case "/auth/validate-token" -> AuthApi.validateToken(request);
            case "/auth/callsign-login-feedback" -> AuthApi.callsignLoginFeedback(request);
            case "/auth/login" -> AuthApi.login(request);
            case "/auth/settings" -> AuthApi.settings(request);
            case "/citizens" -> CitizenApi.citizens(request);
            case "/officers" -> OfficerApi.officers(request);
            case "/emergency-calls" -> EmergencyCallApi.emergencyCalls(request);
            default -> FileServer.serveFile(request, "/404");
        };
    }
}