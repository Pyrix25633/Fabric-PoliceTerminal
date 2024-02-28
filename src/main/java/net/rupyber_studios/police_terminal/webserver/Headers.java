package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Headers {
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(.*): (.*)$");
    private static final Pattern COOKIE_PATTERN = Pattern.compile("^(.*)=(.*)$");

    private final Map<String, String> headers;
    private final Map<String, String> cookies;

    public Headers(@NotNull InputStream input) throws IOException {
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        this.parse(input);
        this.parseCookies();
    }

    private void parse(@NotNull InputStream input) throws IOException {
        int b;
        StringBuilder builder = new StringBuilder();
        short crlf = 0;
        // Waiting for \r\n\r\n to close headers
        while((b = input.read()) >= 0) {
            if(b == (int)'\r' && input.read() == (int)'\n') {
                crlf++;
                if(crlf > 1) break;
                String header = builder.toString();
                builder = new StringBuilder();
                Matcher matcher = HEADER_PATTERN.matcher(header);
                if(matcher.find())
                    headers.put(matcher.group(1), matcher.group(2));
            }
            else {
                crlf = 0;
                builder.append((char)b);
            }
        }
    }

    private void parseCookies() {
        String rawCookies = WebServer.parseUrlEncodedString(headers.get("Cookie"));
        if(rawCookies == null) return;
        for(String rawCookie : rawCookies.split("; ")) {
            Matcher matcher = COOKIE_PATTERN.matcher(rawCookie);
            if(matcher.find())
                cookies.put(matcher.group(1), matcher.group(2));
        }
    }

    public @Nullable String get(String name) {
        return headers.get(name);
    }

    public @Nullable String getCookie(String name) {
        return cookies.get(name);
    }

    public WebToken getWebToken() throws Exceptions.HttpException {
        return new WebToken(getCookie("token"));
    }
}