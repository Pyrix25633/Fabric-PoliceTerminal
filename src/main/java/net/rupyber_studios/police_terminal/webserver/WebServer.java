package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebServer {
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static void handleRequest(Socket socket) {
        Thread worker = new Thread(() -> {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                Request request = new Request(input);

                Response response;
                try {
                    if (request.requestLine.uri.cleanApiUri != null) {
                        response = ApiServer.serveApi(request);
                    } else {
                        response = switch (request.requestLine.uri.cleanUri) {
                            case "/" -> FileServer.serveFile(request, "index.html");
                            case "/login" -> FileServer.serveFile(request, "login.html");
                            case "/citizens" -> FileServer.serveFile(request, "citizens.html");
                            case "/officers" -> FileServer.serveFile(request, "officers.html");
                            case "/callouts" -> FileServer.serveFile(request, "callouts.html");
                            case "/emergency-calls" -> FileServer.serveFile(request, "emergency-calls.html");
                            case "/manual" -> FileServer.serveFile(request, "manual.html");
                            default -> FileServer.serveFile(request);
                        };
                    }
                } catch(Exceptions.HttpException e) {
                    response = e.getResponse();
                }
                response.writeTo(output);

                input.close();
                output.close();
                socket.close();
            } catch(SSLException ignored) {
            } catch(Exception e) {
                PoliceTerminal.LOGGER.error("Error processing request: ", e);
            }
        });
        worker.start();
    }

    public static @Nullable String parseUrlEncodedString(String string) {
        if(string == null) return null;
        return string.replaceAll("%5B", "[")
                .replaceAll("%5D", "]")
                .replaceAll("%20", " ");
    }
}