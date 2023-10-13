package net.deechael.dodo.network;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.deechael.dodo.API;
import net.deechael.dodo.utils.LoggerUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Requester {
    private final Gson gson = new Gson();
    @Getter
    private final OkHttpClient client;
    @Getter
    private final int clientId;
    @Getter
    private final String token;

    public Requester(OkHttpClient client, int clientId, String token) {
        this.client = client;
        this.clientId = clientId;
        this.token = token;
    }

    public JsonObject executeRequest(Route route) {
        Request.Builder builder = new Request.Builder()
                .header("Authorization", "Bot " + getClientId() + "." + getToken())
                .header("Content-type", route.getContentType());
        Map<String, String> routeHeaders = route.getHeaders();
        for (String key : routeHeaders.keySet()) {
            builder.header(key, routeHeaders.get(key));
        }
        if (Objects.equals(route.getContentType(), "multipart/form-data")) {
            builder.post(new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", route.getFile().getName(),
                            RequestBody.create(route.getFile(),
                                    MediaType.parse("multipart/form-data")))
                    .build());
        } else {
            builder.post(RequestBody
                    .create(gson.toJson(route.getParams()),
                            MediaType.get("application/json")));
        }
        Request req = builder.url(API.BASE_URL + route.getRoute()).build();
        Call call = getClient().newCall(req);
        try {
            Response response = call.execute();
            if (!response.isSuccessful()){}
                //LOGGER.error("Failed to execute: " + route.getRoute(), new RuntimeException("Code: " + response.code()));
            JsonObject object =  new JsonParser().parse(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
            if (object.get("status").getAsInt() != 0){}
                //LOGGER.error("Dodo error when executing " + route.getRoute(), new RuntimeException(object.get("message").getAsString()));
            return object;
        } catch (IOException e) {{}
            //LOGGER.error("Error was thrown when executing: " + route.getRoute(), e);
        }
        return new JsonObject();
    }

    public Future<JsonObject> executeRequestAsync(Route route) {
        // FIXME
        Request.Builder builder = new Request.Builder()
                .post(RequestBody.create(new byte[0], MediaType.get("application/json")))
                .header("Authorization", "Bot " + getClientId() + "." + getToken())
                .header("Content-type", route.getContentType());
        Map<String, String> routeHeaders = route.getHeaders();
        for (String key : routeHeaders.keySet()) {
            builder.header(key, routeHeaders.get(key));
        }
        RequestedFuture future = new RequestedFuture();
        Call call = getClient().newCall(builder.url(API.BASE_URL + route.getRoute()).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.cancel(true);
                //LOGGER.error("Error was thrown when executing: " + route.getRoute(), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.cancel(true);
                   // LOGGER.error("Failed to execute: " + route.getRoute(), new RuntimeException("Code: " + response.code()));
                }
                JsonObject object =  new JsonParser().parse(Objects.requireNonNull(response.body()).string()).getAsJsonObject();
                if (object.get("status").getAsInt() != 0) {
                    future.cancel(true);
                   // LOGGER.error("Dodo error when executing " + route.getRoute(), new RuntimeException(object.get("message").getAsString()));
                }
                future.done(object);
            }
        });
        return future;
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
        public JsonObject get() throws InterruptedException, ExecutionException {
            while (true) {
                if (isCancelled() || isDone())
                    break;
            }
            return result;
        }

        @Override
        public JsonObject get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
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
