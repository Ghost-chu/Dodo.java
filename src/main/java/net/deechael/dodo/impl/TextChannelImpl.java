package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import net.deechael.dodo.API;
import net.deechael.dodo.api.TextChannel;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.network.Route;

public class TextChannelImpl extends ChannelImpl implements TextChannel {

    public TextChannelImpl(Gateway gateway, JsonObject info) {
        super(gateway, info);
    }

    @Override
    public String send(Message content) {
        Route route = API.V2.Channel.messageSend()
                .param("channelId", getId())
                .param("messageType", content.getType().getCode())
                .param("messageBody", content.get());
        return gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
    }

    @Override
    public String send(Message content, String repliedMessageId) {
        Route route = API.V2.Channel.messageSend()
                .param("channelId", getId())
                .param("messageType", content.getType().getCode())
                .param("messageBody", content.get())
                .param("referencedMessageId", repliedMessageId);
        return gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
    }

    @Override
    public String sendPrivate(Message content, String dodoId) {
        Route route = API.V2.Channel.messageSend()
                .param("channelId", getId())
                .param("messageType", content.getType().getCode())
                .param("messageBody", content.get())
                .param("dodoSourceId", dodoId);
        return gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
    }

    @Override
    public void addReaction(String messageId, String emoji) {
        Route route = API.V2.Channel.messageSend()
                .param("messageId", getId())
                .param("emoji", emoji);
        gateway.executeRequest(route);
    }

    @Override
    public void removeReaction(String messageId, String emoji) {
        Route route = API.V2.Channel.messageSend()
                .param("messageId", getId())
                .param("emoji", emoji);
        gateway.executeRequest(route);
    }

    @Override
    public void removeReaction(String messageId, String emoji, String dodoId) {
        Route route = API.V2.Channel.messageSend()
                .param("messageId", getId())
                .param("emoji", emoji)
                .param("dodoSourceId", dodoId);
        gateway.executeRequest(route);
    }

}
