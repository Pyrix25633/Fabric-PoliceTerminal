package net.rupyber_studios.police_terminal.webserver;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URI {
    private static final Pattern QUERY_STRING_PATTERN = Pattern.compile("^(.+)\\?(.*)$");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("^(.+)=(.+)$");
    private static final Pattern FRAGMENT_PATTERN = Pattern.compile("^(.+)#(.*)$");
    private static final Pattern API_URI_PATTERN = Pattern.compile("^/api(.*)$");

    public String cleanUri;
    private HashMap<String, String> parameters;
    public String fragment;
    public String cleanApiUri;

    public URI(String uri) {
        this.cleanUri = uri;
        this.parameters = new HashMap<>();
        this.fragment = null;
        this.cleanApiUri = null;
        Matcher matcher = QUERY_STRING_PATTERN.matcher(uri);
        if(matcher.find()) {
            this.cleanUri = matcher.group(1);
            this.parseQueryString(matcher.group(2));
        }
        matcher = FRAGMENT_PATTERN.matcher(cleanUri);
        if(matcher.find()) {
            this.cleanUri = matcher.group(1);
            this.fragment = matcher.group(2);
        }
        matcher = API_URI_PATTERN.matcher(cleanUri);
        if(matcher.find())
            cleanApiUri = matcher.group(1);
    }

    @Contract(pure = true)
    private void parseQueryString(@NotNull String queryString) {
        for(String parameter : queryString.split("&")) {
            Matcher matcher = PARAMETER_PATTERN.matcher(parameter);
            if(matcher.find())
                parameters.put(matcher.group(1), matcher.group(2));
        }
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}