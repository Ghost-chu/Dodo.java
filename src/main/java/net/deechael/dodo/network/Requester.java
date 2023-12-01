package net.deechael.dodo.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import net.deechael.dodo.API;
import net.deechael.dodo.utils.LoggerUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Requester {

    private final static org.slf4j.Logger LOGGER = LoggerUtils.getLogger(Requester.class);
    private final Gson gson = new Gson();
    @Getter
    private final int clientId;
    @Getter
    private final String token;

    public Requester(int clientId, String token) {
        this.clientId = clientId;
        this.token = token;
    }

    public JsonObject executeRequest(Route route) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bot " + getClientId() + "." + getToken());
        //headers.put("Content-type", route.getContentType());
        Map<String, String> routeHeaders = route.getHeaders();
        for (String key : routeHeaders.keySet()) {
            headers.put(key, routeHeaders.get(key));
        }
        HttpRequest request;
        if (Objects.equals(route.getContentType(), "multipart/form-data")) {
            headers.clear();
            headers.put("Authorization", "Bot " + getClientId() + "." + getToken());
            request = Unirest.post(API.BASE_URL + route.getRoute())
                    .headers(headers)
                    .field("file", route.getFile(), route.getFile().getName());
        } else {
            request = Unirest.post(API.BASE_URL + route.getRoute())
                    .headers(headers)
                    .connectTimeout(15*1000)
                    .socketTimeout(30*1000)
                    .contentType("application/json")
                    .body(gson.toJson(route.getParams()));
        }
        HttpResponse<String> req = request.asString();
        if (req.isSuccess()) {
            JsonObject object = JsonParser.parseString(Objects.requireNonNull(req.getBody())).getAsJsonObject();
            if (object.get("status").getAsInt() != 0) {
                LOGGER.warn("Dodo error when executing " + route.getRoute() + " with params " + gson.toJson(route.getParams())
                        , new RuntimeException(object.toString()));
            }
            return object;
        }
        LOGGER.warn("DoDo request failed");
        return new JsonObject();
    }

    private static class RequestedFuture implements Future<JsonObject> {

        private boolean done = false;
        private boolean cancel = false;
        private JsonObject result = new JsonObject();

        public void done(JsonObject object) {
            this.done = true;
            this.result = object;
        }

        @Override
        public boolean cancel(boolean b) {
            boolean temp = this.cancel;
            this.cancel = b;
            return temp;
        }

        @Override
        public boolean isCancelled() {
            return this.cancel;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public JsonObject get() {
            while (true) {
                if (isCancelled() || isDone())
                    break;
            }
            return result;
        }

        @Override
        public JsonObject get(long l, TimeUnit timeUnit) {
            long startTime = System.currentTimeMillis();
            long milli = timeUnit.convert(l, TimeUnit.MILLISECONDS);
            while (true) {
                if (isCancelled() || isDone() || System.currentTimeMillis() - startTime >= milli)
                    break;
            }
            return result;
        }
    }

}
