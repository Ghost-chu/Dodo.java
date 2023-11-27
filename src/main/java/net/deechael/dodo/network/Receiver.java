package net.deechael.dodo.network;

import lombok.Getter;
import okhttp3.OkHttpClient;

public abstract class Receiver {

    @Getter
    private final OkHttpClient client;
    @Getter
    private final int clientId;
    private final String token;

    public Receiver(OkHttpClient client, int clientId, String token) {
        this.client = client;
        this.clientId = clientId;
        this.token = token;
    }

    protected String getToken() {
        return this.token;
    }

    public abstract void start();

    public abstract boolean isStart();

    public abstract void stop();

}
