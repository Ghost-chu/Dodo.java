package net.deechael.dodo.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NonNull;
import net.deechael.dodo.API;
import net.deechael.useless.function.parameters.Parameter;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WebSocketReceiver extends Receiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketReceiver.class);

    private final PacketListener listener = new PacketListener(this);

    private final Parameter<JsonObject> solver;

    private boolean started = false;
    private boolean triedToStart = false;

    @Getter
    private WebSocket webSocket;

    public WebSocketReceiver(OkHttpClient client, int clientId, String token, Parameter<JsonObject> solver) {
        super(client, clientId, token);
        this.solver = solver;
    }

    @Override
    public void start() {
        if (triedToStart) {
            LOGGER.warn("Another start process is running.");
            return;
        }
        LOGGER.info("Connecting...");
        triedToStart = true;
        String auth = "Bot " + getClientId() + "." + getToken();
        Request req = new Request.Builder()
                .post(RequestBody.create(new byte[0], MediaType.get("application/json")))
                .header("Authorization", auth)
                .header("Content-type", "application/json;charset=utf-8")
                .header("Content-Encoding", "UTF-8")
                .url(API.BASE_URL + API.V2.Websocket.connection().getRoute()).build();
        Call call = getClient().newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                try {
                    Thread.sleep(10000);
                    CompletableFuture.supplyAsync(()->{
                        start();
                        return null;
                    });
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                LOGGER.error("Failed to fetch the websocket url", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                LOGGER.info("Fetched websocket url successfully");
                String resp = Objects.requireNonNull(response.body()).string();
                JsonObject body = JsonParser.parseString(resp).getAsJsonObject();
                try {
                    String url = body.getAsJsonObject("data").get("endpoint").getAsString();
                    LOGGER.info("Websocket url: " + url);
                    websocketStart(url);
                }catch (NullPointerException e){
                    LOGGER.warn("Failed to retrieve the websocket url: {}",resp);
                }

            }
        });
    }

    @Override
    public boolean isStart() {
        return started;
    }

    @Override
    public void stop() {
        this.webSocket.close(1000,null);
    }

    private void websocketStart(String url) {
        LOGGER.info("Starting websocket...");
        this.webSocket = getClient().newWebSocket(
                new Request.Builder()
                        .get()
                        .header("Content-type", "application/json;charset=utf-8")
                        .header("Content-Encoding", "UTF-8")
                        .url(url)
                        .build(), this.listener);
    }

    private static final class PacketListener extends WebSocketListener {

        private final WebSocketReceiver receiver;

        public PacketListener(WebSocketReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            LOGGER.debug("Connected successfully");
            receiver.triedToStart = false;
            receiver.started = true;
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            JsonObject object = JsonParser.parseString(text).getAsJsonObject();
            if (object.get("type").getAsInt() != 0)
                return;
            receiver.solver.apply(object);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            this.onMessage(webSocket, new String(bytes.toByteArray(),StandardCharsets.UTF_8));
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            LOGGER.warn("WebSocket connection has been closed");
            receiver.triedToStart = false;
            receiver.started = false;
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable throwable, Response response) {
            webSocket.close(1000, null);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LOGGER.warn("Failed on websocket, reconnecting...", throwable);
            receiver.triedToStart = false;
            receiver.started = false;
            receiver.start();

        }

    }

}
