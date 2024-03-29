package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileServer {
    public static final String CACHE_CONTROL_HEADER = "Cache-Control: public, max-age=600" + WebServer.CRLF;
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public static void serveFile(@NotNull Request request, OutputStream output) throws IOException {
        serveFile(request, request.requestLine.uri.cleanUri, output);
    }

    public static void serveFile(@NotNull Request request, String path, OutputStream output) throws IOException {
        InputStream input = classLoader.getResourceAsStream(Paths.get("pages", path).toString());
        String response = WebServer.RESPONSE_200;

        if(request.requestLine.method != Method.GET) {
            input = classLoader.getResourceAsStream("pages/405.html");
            response = WebServer.RESPONSE_405;
        }

        if(input == null) {
            input = classLoader.getResourceAsStream("pages/404.html");
            if(input == null) return;
            response = WebServer.RESPONSE_404;
        }

        byte[] content = input.readAllBytes();

        response += WebServer.getContentLengthHeader(content) + getContentTypeHeader(path) +
                CACHE_CONTROL_HEADER + WebServer.CORS_HEADERS + WebServer.CRLF;

        output.write(response.getBytes());
        output.write(content);
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }

    public static @NotNull String getContentTypeHeader(String path) {
        Matcher matcher = Pattern.compile("^.*\\.(html|css|js|ttf|otf|svg)$").matcher(path);
        if(matcher.find()) {
            return "Content-Type: " + switch(matcher.group(1)) {
                case "css" -> "text/css";
                case "js" -> "text/javascript";
                case "ttf" -> "font/ttf";
                case "otf" -> "font/otf";
                case "svg" -> "image/svg+xml";
                default -> "text/html";
            } + WebServer.CRLF;
        }
        else return "";
    }
}