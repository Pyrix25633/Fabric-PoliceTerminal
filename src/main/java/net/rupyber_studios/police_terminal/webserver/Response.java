package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class Response {
    public final Status status;
    public final Headers headers;
    public final byte[] body;

    public Response(Status status, @NotNull JSONObject body) {
        this.status = status;
        this.headers = new Headers();
        this.headers.set(WebServer.CONTENT_TYPE_HEADER, "application/json");
        this.setSecurityHeaders();
        this.body = body.toString().getBytes();
    }

    public Response(Status status, byte[] body, String contentType) {
        this.status = status;
        this.headers = new Headers();
        this.headers.set(WebServer.CONTENT_TYPE_HEADER, contentType);
        this.headers.set("Cache-Control", "public, max-age=600");
        this.setSecurityHeaders();
        this.body = body;
    }

    public Response(Status status) {
        this.status = status;
        this.headers = new Headers();
        this.body = new byte[]{};
    }

    private void setSecurityHeaders() {
        this.headers.set("Content-Security-Policy", "default-src 'self'; base-uri 'self'; " +
                "font-src 'self' https:; frame-ancestors 'self'; img-src 'self' data:; object-src 'none'; " +
                "script-src 'self' https:; script-src-attr 'none'; style-src 'self' https: data: 'unsafe-inline'");
        this.headers.set("Cross-Origin-Embedder-Policy", "require-corp");
        this.headers.set("Cross-Origin-Opener-Policy", "same-origin");
        this.headers.set("Cross-Origin-Resource-Policy", "same-origin");
    }

    public void writeTo(@NotNull OutputStream output) throws IOException {
        output.write((WebServer.HTTP_VERSION + " " + status.code + WebServer.CRLF).getBytes());
        headers.set(WebServer.CONTENT_LENGTH_HEADER, String.valueOf(body.length));
        headers.writeTo(output);
        output.write(body);
        output.write((WebServer.CRLF + WebServer.CRLF).getBytes());
    }
}