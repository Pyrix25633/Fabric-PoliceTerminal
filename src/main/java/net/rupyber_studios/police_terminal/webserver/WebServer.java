package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String RESPONSE_500 = HTTP_VERSION + " 500 Server Error " + CRLF;
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("^(.*) (.*) HTTP/(.*)$");
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(.*): (.*)$");

    @Contract(pure = true)
    public static @NotNull String getContentLengthHeader(@NotNull String content) {
        return CONTENT_LENGTH_HEADER + content.getBytes().length + CRLF;
    }

    public static void handleRequest(Socket socket) {
        Thread worker = new Thread(() -> {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                RequestLine requestLine = parseRequestLine(input);

                if(requestLine == null) {
                    send400AndClose(input, output, socket);
                    return;
                }

                HashMap<String, String> headers = parseHeaders(input);

                switch(requestLine.uri()) {
                    case "/" -> FileServer.serveFile(requestLine.method(), "index.html", output);
                    default -> FileServer.serveFile(requestLine.method(), requestLine.uri(), output);
                }

                input.close();
                output.close();
                socket.close();
            } catch(Exception e) {
                PoliceTerminal.LOGGER.error("Error processing request: ", e);
            }
        });
        worker.start();
    }

    public static void send400AndClose(@NotNull InputStream input, @NotNull OutputStream output,
                                       @NotNull Socket socket) throws IOException {
        output.write(RESPONSE_400.getBytes());
        input.close();
        output.close();
        socket.close();
    }

    public static @Nullable RequestLine parseRequestLine(@NotNull InputStream input) throws IOException {
        int b;
        StringBuilder builder = new StringBuilder();

        // Reading method, URI and HTTP version, accepting only HTTP >= 1.1
        while((b = input.read()) >= 0) {
            if(b == (int)'\r' && input.read() == (int)'\n')
                break;
            else
                builder.append((char)b);
        }

        Matcher matcher = REQUEST_LINE_PATTERN.matcher(builder.toString());
        if(matcher.find()) {
            if(Float.parseFloat(matcher.group(3)) < 1.1F)
                return null;
            return new RequestLine(matcher.group(1), matcher.group(2));
        }
        else
            return null;
    }

    public static @NotNull HashMap<String, String> parseHeaders(@NotNull InputStream input) throws IOException {
        int b;
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> headers = new HashMap<>();
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

        return headers;
    }
}