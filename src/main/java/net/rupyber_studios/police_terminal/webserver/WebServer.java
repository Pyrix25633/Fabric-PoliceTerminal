package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebServer {
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\n\r";
    public static final String RESPONSE_200 = HTTP_VERSION + " 200 OK " + CRLF;
    public static final String RESPONSE_201 = HTTP_VERSION + " 201 Created " + CRLF;
    public static final String RESPONSE_400 = HTTP_VERSION + " 400 Bad Request " + CRLF;
    public static final String RESPONSE_401 = HTTP_VERSION + " 401 Unauthorized " + CRLF;
    public static final String RESPONSE_403 = HTTP_VERSION + " 403 Forbidden " + CRLF;
    public static final String RESPONSE_404 = HTTP_VERSION + " 404 Not Found " + CRLF;
    public static final String RESPONSE_500 = HTTP_VERSION + " 500 Server Error " + CRLF;
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";

    @Contract(pure = true)
    public static @NotNull String getContentLengthHeader(@NotNull String content) {
        return CONTENT_LENGTH_HEADER + content.getBytes().length + CRLF;
    }

    public static void handleRequest(Socket socket) {
        Thread worker = new Thread(() -> {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                int b;
                StringBuilder builder = new StringBuilder();
                short crlf = 0;
                boolean finishedHeaders = false;
                // Waiting for \n\r\n\r to close headers and then \n\r\n\r to close request
                while((b = input.read()) >= 0) {
                    PoliceTerminal.LOGGER.warn(String.valueOf((char)b));
                    builder.append(b);
                    if(b == (int)'\n' && input.read() == (int)'\r') {
                        crlf++;
                        PoliceTerminal.LOGGER.error("CRLF");
                        if(crlf > 1) {
                            PoliceTerminal.LOGGER.error("2 CRLF");
                            if(finishedHeaders) break;
                            else finishedHeaders = true;
                        }
                    }
                    else crlf = 0;
                }
                PoliceTerminal.LOGGER.info(builder.toString());

                serveLoginPage(input, output);

                input.close();
                output.close();
                socket.close();
            } catch(Exception e) {
                PoliceTerminal.LOGGER.error("Error processing request: ", e);
            }
        });
        worker.start();
    }

    public static void serveLoginPage(InputStream input, @NotNull OutputStream output) throws IOException {
        String content = "Login Page";
        String response = RESPONSE_200 + getContentLengthHeader(content) + CRLF + content + CRLF + CRLF;
        output.write(response.getBytes());
    }
}