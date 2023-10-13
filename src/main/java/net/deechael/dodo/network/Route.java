package net.deechael.dodo.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Route {

    private final String method;

    private final String route;

    private String contentType = "application/json";

    private final JsonObject params = new JsonObject();

    private final Map<String, String> headers = new HashMap<>();

    private File file;

    public Route(String method, String url) {
        this.method = method;
        this.route = url;
    }

    public JsonObject getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Route param(String name, Object value) {
        if (value == null)
            return this;
        if (value instanceof File) {
            if (!name.equals("file"))
                return this;
            this.file = (File) value;
            if (!file.exists())
                return this;
            this.headers.put("Content-Length", "" + file.length());
            this.contentType = "multipart/form-data";
        } else {
            if (value instanceof JsonElement) {
                this.params.add(name, (JsonElement) value);
            } else if (value instanceof Number) {
                this.params.addProperty(name, (Number) value);
            } else if (value instanceof Boolean) {
                this.params.addProperty(name, (Boolean) value);
            } else if (value instanceof Character) {
                this.params.addProperty(name, (Character) value);
            } else if (value.getClass().isArray()) {
                this.params.add(name,  new JsonParser().parse(Arrays.toString((Object[]) value)));
            } else {
                this.params.addProperty(name, value.toString());
            }
        }
        return this;
    }

    public Route header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    // Dodo's requests must use POST
    public String getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return this.route;
    }

}
