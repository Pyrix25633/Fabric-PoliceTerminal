package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.webserver.api.AuthApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        this();
        this.parse(input);
        this.parseCookies();
    }

    public Headers() {
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
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

    public void set(String name, String value) {
        headers.put(name, value);
    }

    public @Nullable String getCookie(String name) {
        return cookies.get(name);
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public WebToken getWebToken() throws Exceptions.HttpException {
        return new WebToken(getCookie(AuthApi.WEB_TOKEN_COOKIE_NAME));
    }

    public void setWebToken(@NotNull JSONObject token) {
        setCookie(AuthApi.WEB_TOKEN_COOKIE_NAME, token.toString());
    }

    public void writeTo(OutputStream output) throws IOException {
        for(String name : headers.keySet())
            output.write((name + ": " + headers.get(name) + WebServer.CRLF).getBytes());
        for(String name : cookies.keySet())
            output.write(("Set-Cookie: " + name + "=" + cookies.get(name) + "; Path=/; SameSite=Strict; HttpOnly" +
                    WebServer.CRLF).getBytes());
        output.write(WebServer.CRLF.getBytes());
    }
}