package net.deechael.dodo.gate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.deechael.dodo.network.Requester;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.network.WebSocketReceiver;

public class Gateway {

    private final Requester requester;
    private final WebSocketReceiver receiver;

    public Gateway(Requester requester, WebSocketReceiver receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }

    public WebSocketReceiver getReceiver() {
        return receiver;
    }

    public Requester getRequester() {
        return requester;
    }

    public JsonElement executeRequest(Route route) {
        JsonObject result = this.requester.executeRequest(route);
        if (result.has("data")) {
            return result.get("data");
        }
        return new JsonObject();
    }

}
