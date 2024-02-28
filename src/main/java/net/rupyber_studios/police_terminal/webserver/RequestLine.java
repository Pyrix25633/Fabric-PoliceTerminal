package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLine {
    private static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("^(.*) (.*) HTTP/(.*)$");

    public Method method;
    public URI uri;

    public RequestLine(@NotNull InputStream input) throws IOException, Exceptions.BadRequestException {
        int b;
        StringBuilder builder = new StringBuilder();
        // Reading method, URI and HTTP version, accepting only HTTP >= 1.1
        while((b = input.read()) >= 0) {
            if(b == (int)'\r' && input.read() == (int)'\n')
                break;
            else
                builder.append((char)b);
        }
        Matcher matcher = REQUEST_LINE_PATTERN.matcher(WebServer.parseUrlEncodedString(builder.toString()));
        if(matcher.find()) {
            if(Float.parseFloat(matcher.group(3)) < 1.1F)
                throw new Exceptions.BadRequestException();
            this.method = Method.fromString(matcher.group(1));
            this.uri = new URI(matcher.group(2));
        }
        else
            throw new Exceptions.BadRequestException();
    }
}