package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileServer {
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public static void serveFile(@NotNull String method, String path, OutputStream output) throws IOException {
        InputStream input = classLoader.getResourceAsStream(Paths.get("pages", path).toString());
        String response = WebServer.RESPONSE_200;

        if(!method.equals("GET")) {
            input = classLoader.getResourceAsStream("pages/405.html");
            response = WebServer.RESPONSE_405;
        }

        if(input == null) {
            input = classLoader.getResourceAsStream("pages/404.html");
            if(input == null) return;
            response = WebServer.RESPONSE_404;
        }

        String content = new String(input.readAllBytes());
        response += WebServer.getContentLengthHeader(content) + getContentTypeHeader(path) + WebServer.CRLF + content +
                WebServer.CRLF + WebServer.CRLF;

        output.write(response.getBytes());
    }

    public static @NotNull String getContentTypeHeader(String path) {
        Matcher matcher = Pattern.compile("^.*\\.(html|css|js)$").matcher(path);
        if(matcher.find()) {
            return "Content-Type: text/" + switch(matcher.group(1)) {
                case "css" -> "css";
                case "js" -> "javascript";
                default -> "html";
            } + WebServer.CRLF;
        }
        else return "";
    }
}