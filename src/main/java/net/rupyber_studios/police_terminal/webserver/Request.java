package net.rupyber_studios.police_terminal.webserver;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Request {
    public RequestLine requestLine;
    public Headers headers;
    public String body;

    public Request(InputStream input) throws Exceptions.BadRequestException, IOException {
        this.requestLine = new RequestLine(input);
        this.headers = new Headers(input);
        this.parseBody(input);
    }

    private void parseBody(InputStream input) throws IOException {
        String contentLengthString = headers.get("Content-Length");
        if(contentLengthString == null) {
            this.body = null;
            return;
        }
        int contentLength = Integer.parseInt(contentLengthString);
        if(contentLength <= 0) {
            this.body = null;
            return;
        }
        int b;
        StringBuilder body = new StringBuilder();
        // Reading up to specified Content-Length
        while(body.length() < contentLength) {
            b = input.read();
            if(b < 0) break;
            body.append((char)b);
        }
        this.body = body.toString();
    }

    public JSONObject getJsonBody() {
        return new JSONObject(body);
    }
}