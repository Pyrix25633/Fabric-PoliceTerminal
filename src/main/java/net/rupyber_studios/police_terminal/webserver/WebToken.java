package net.rupyber_studios.police_terminal.webserver;

import org.json.JSONObject;

public class WebToken {
    public int id;
    public String token;

    public WebToken(String cookie) throws Exceptions.HttpException {
        JSONObject jsonToken = new JSONObject(cookie);
        this.id = Exceptions.getInt(jsonToken, "id");
        this.token = Exceptions.getString(jsonToken, "token");
    }
}