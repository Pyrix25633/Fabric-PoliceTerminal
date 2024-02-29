package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebServer {
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";
    public static final String RESPONSE_200 = HTTP_VERSION + " 200 OK " + CRLF;
    public static final String RESPONSE_201 = HTTP_VERSION + " 201 Created " + CRLF;
    public static final String RESPONSE_400 = HTTP_VERSION + " 400 Bad Request " + CRLF;
    public static final String RESPONSE_401 = HTTP_VERSION + " 401 Unauthorized " + CRLF;
    public static final String RESPONSE_403 = HTTP_VERSION + " 403 Forbidden " + CRLF;
    public static final String RESPONSE_404 = HTTP_VERSION + " 404 Not Found " + CRLF;
    public static final String RESPONSE_405 = HTTP_VERSION + " 405 Method Not Allowed " + CRLF;
    public static final String RESPONSE_500 = HTTP_VERSION + " 500 Internal Server Error " + CRLF;
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    public static final String CORS_HEADERS = "Content-Security-Policy: default-src 'self'; base-uri 'self'; " +
            "font-src 'self' https:; frame-ancestors 'self'; img-src 'self' data:; object-src 'none'; " +
            "script-src 'self' https:; script-src-attr 'none'; style-src 'self' https: data: 'unsafe-inline'" + CRLF +
            "Cross-Origin-Embedder-Policy: require-corp" + CRLF +
            "Cross-Origin-Opener-Policy: same-origin" + CRLF +
            "Cross-Origin-Resource-Policy: same-origin" + CRLF;
    public static final String SET_COOKIE_HEADER = "Set-Cookie: ";

    @Contract(pure = true)
    public static @NotNull String getContentLengthHeader(byte @NotNull [] content) {
        return CONTENT_LENGTH_HEADER + content.length + CRLF;
    }

    public static void handleRequest(Socket socket) {
        Thread worker = new Thread(() -> {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                Request request = new Request(input);

                if(request.requestLine.uri.cleanApiUri != null) {
                    ApiServer.serveApi(request, output);
                }
                else {
                    switch(request.requestLine.uri.cleanUri) {
                        case "/" -> FileServer.serveFile(request, "index.html", output);
                        case "/login" -> FileServer.serveFile(request, "login.html", output);
                        case "/citizens" -> FileServer.serveFile(request, "citizens.html", output);
                        case "/officers" -> FileServer.serveFile(request, "officers.html", output);
                        case "/callouts" -> FileServer.serveFile(request, "callouts.html", output);
                        case "/emergency-calls" -> FileServer.serveFile(request, "emergency-calls.html", output);
                        case "/manual" -> FileServer.serveFile(request, "manual.html", output);
                        default -> FileServer.serveFile(request, output);
                    }
                }

                input.close();
                output.close();
                socket.close();
            } catch(SSLException ignored) {
            }  catch(Exception e) {
                PoliceTerminal.LOGGER.error("Error processing request: ", e);
            }
        });
        worker.start();
    }

    public static void send400(@NotNull OutputStream output) throws IOException {
        sendError(RESPONSE_400, output);
    }

    public static void send401(@NotNull OutputStream output) throws IOException {
        sendError(RESPONSE_401, output);
    }

    public static void send404(@NotNull OutputStream output) throws IOException {
        sendError(RESPONSE_404, output);
    }

    public static void send405(@NotNull OutputStream output) throws IOException {
        sendError(RESPONSE_405, output);
    }

    public static void send500(@NotNull OutputStream output) throws IOException {
        sendError(RESPONSE_500, output);
    }

    public static void sendError(String error, @NotNull OutputStream output) throws IOException {
        output.write((error + getContentLengthHeader("".getBytes()) + CRLF).getBytes());
    }

    public static @Nullable String parseUrlEncodedString(String string) {
        if(string == null) return null;
        return string.replaceAll("%5B", "[")
                .replaceAll("%5D", "]")
                .replaceAll("%20", " ");
    }
}