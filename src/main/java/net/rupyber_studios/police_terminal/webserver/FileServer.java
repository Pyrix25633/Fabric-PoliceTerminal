package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileServer {
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public static @NotNull Response serveFile(@NotNull Request request) throws Exceptions.HttpException {
        return serveFile(request, request.requestLine.uri.cleanUri);
    }

    public static @NotNull Response serveFile(@NotNull Request request, String path) throws Exceptions.HttpException {
        InputStream input = classLoader.getResourceAsStream(Paths.get("pages", path)
                .toString());
        Status status = Status.OK;

        if(request.requestLine.method != Method.GET) {
            input = classLoader.getResourceAsStream("pages/405.html");
            status = Status.METHOD_NOT_ALLOWED;
        }

        if(input == null) {
            input = classLoader.getResourceAsStream("pages/404.html");
            if(input == null) throw new Exceptions.InternalServerException();
            status = Status.NOT_FOUND;
        }
        try {
            byte[] content = input.readAllBytes();
            return new Response(status, content, getContentTypeHeader(path));
        } catch(IOException e) {
            throw new Exceptions.InternalServerException();
        }
    }

    public static @NotNull String getContentTypeHeader(String path) {
        Matcher matcher = Pattern.compile("^.*\\.(html|css|js|ttf|otf|svg)$").matcher(path);
        if(matcher.find()) {
            return switch(matcher.group(1)) {
                case "css" -> "text/css";
                case "js" -> "text/javascript";
                case "ttf" -> "font/ttf";
                case "otf" -> "font/otf";
                case "svg" -> "image/svg+xml";
                default -> "text/html";
            };
        }
        else return "";
    }
}