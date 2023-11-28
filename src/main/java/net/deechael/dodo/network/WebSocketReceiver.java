package net.deechael.dodo.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.SneakyThrows;
import net.deechael.dodo.API;
import net.deechael.useless.function.parameters.Parameter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class WebSocketReceiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketReceiver.class);

    private final Parameter<JsonObject> solver;
    private final int clientId;
    private final String token;

    private boolean started = false;
    private boolean triedToStart = false;

    @Getter
    private PacketListener webSocket;

    private Timer timer = new Timer();

    public WebSocketReceiver(int clientId, String token, Parameter<JsonObject> solver) {
        this.clientId = clientId;
        this.token = token;
        this.solver = solver;
    }

    @SneakyThrows(InterruptedException.class)
    public void start() {
        if (triedToStart) {
            LOGGER.warn("Another start process is running.");
            return;
        }
        LOGGER.info("Connecting...");
        triedToStart = true;
        String auth = "Bot " + clientId + "." + token;
        HttpResponse<String> resp = Unirest.post(API.BASE_URL + API.V2.Websocket.connection().getRoute())
                .header("Authorization", auth)
                .header("Content-type", "application/json;charset=utf-8")
                .header("Content-Encoding", "UTF-8")
                .asString();

        if (resp.isSuccess()) {
            LOGGER.info("Fetched websocket url successfully");
            JsonObject body = JsonParser.parseString(resp.getBody()).getAsJsonObject();
            try {
                String url = body.getAsJsonObject("data").get("endpoint").getAsString();
                LOGGER.info("Websocket url: " + url);
                websocketStart(url);
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (webSocket.isOpen()) {
                            webSocket.sendPing();
                        }
                    }
                },0,25*1000L);
            } catch (NullPointerException | URISyntaxException e) {
                LOGGER.warn("Failed to retrieve the websocket url:" + resp);
            }
        } else {
            Thread.sleep(10000);
            CompletableFuture.supplyAsync(() -> {
                start();
                return null;
            });
        }
    }

    public boolean isStart() {
        return started;
    }

    public void stop() {
        this.webSocket.close(1000, null);
    }

    private void websocketStart(String url) throws URISyntaxException {
        LOGGER.info("Starting websocket...");
        this.webSocket = new PacketListener(new URI(url), this);
        this.webSocket.addHeader("Content-type", "application/json;charset=utf-8");
        this.webSocket.addHeader("Content-Encoding", "UTF-8");
        this.webSocket.setConnectionLostTimeout(60);
        this.webSocket.connect();
    }

    private static final class PacketListener extends WebSocketClient {

        private final WebSocketReceiver receiver;

        public PacketListener(URI uri, WebSocketReceiver receiver) {
            super(uri);
            this.receiver = receiver;
        }


        @Override
        public void onOpen(ServerHandshake handshakedata) {
            LOGGER.info("Connected successfully");
            receiver.triedToStart = false;
            receiver.started = true;
        }

        @Override
        public void onMessage(String message) {
            JsonObject object = JsonParser.parseString(message).getAsJsonObject();
            if (object.get("type").getAsInt() != 0)
                return;
            receiver.solver.apply(object);
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            this.onMessage(new String(bytes.array(), StandardCharsets.UTF_8));
        }


        @Override
        public void onClose(int code, String reason, boolean remote) {
            LOGGER.warn("WebSocket connection has been closed");
            receiver.triedToStart = false;
            receiver.started = false;
            if (remote) {
                receiver.timer.cancel();
                receiver.start();
            }
        }

        @Override
        public void onError(Exception ex) {
            close();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LOGGER.warn("Failed on websocket, reconnecting...", ex);
        }
    }

}
