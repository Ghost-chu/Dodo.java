package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import net.deechael.dodo.API;
import net.deechael.dodo.api.*;
import net.deechael.dodo.command.CommandManager;
import net.deechael.dodo.command.DodoCommand;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.content.TextMessage;
import net.deechael.dodo.event.EventManager;
import net.deechael.dodo.event.Listener;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.history.HistoryManager;
import net.deechael.dodo.history.HistoryMessage;
import net.deechael.dodo.network.Requester;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.network.WebSocketReceiver;
import net.deechael.dodo.types.MessageType;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientImpl implements Client {

    private final Gateway gateway;

    private final EventManager eventManager;
    private final CommandManager commandManager;
    private final HistoryManager historyManager;
    private final WebSocketReceiver websocketReceiver;

    private boolean started = false;

    public ClientImpl(int clientId, String token) {
        this.historyManager = new HistoryManagerImpl();
        this.websocketReceiver =  new WebSocketReceiver(clientId, token, this::pkgReceive);
        this.gateway = new Gateway(new Requester( clientId, token),websocketReceiver);
        this.eventManager = new EventManager(this);
        this.commandManager = new CommandManager();
    }

    @Override
    public void start() {
        if (started)
            return;
        started = true;
        this.gateway.getReceiver().start();
    }

    @Override
    public boolean isStart() {
        return started && this.gateway.getReceiver().isStart();
    }

    @Override
    public void stop() {

    }

    @Override
    public Gateway getGateway() {
        return this.gateway;
    }

    @Override
    public void addEventListener(Listener listener) {
        this.eventManager.addListener(listener);
    }

    @Override
    public void unregisterEventListener(Listener listener) {
        this.eventManager.unregisterListener(listener);
    }

    @Override
    public void unregisterAllEventListeners(){
        this.eventManager.unregisterAllListeners();
    }

    @Override
    public void registerCommand(DodoCommand command) {
        this.commandManager.register(command);
    }

    @Override
    public Island fetchIsland(String islandId) {
        Route route = API.V2.Island.info()
                .param("islandSourceId", islandId);
        JsonObject info = gateway.executeRequest(route).getAsJsonObject();
        return new IslandImpl(gateway, info);
    }

    @Override
    public Channel fetchChannel(String islandId, String channelId) {
        Route route = API.V2.Channel.info()
                .param("islandSourceId", islandId)
                .param("channelId", channelId);
        JsonObject info = gateway.executeRequest(route).getAsJsonObject();
        if (info.get("channelType").getAsInt() == 1) {
            return new TextChannelImpl(gateway, info);
        } else if (info.get("channelType").getAsInt() == 2) {
            return new VoiceChannelImpl(gateway, info);
        } else if (info.get("channelType").getAsInt() == 4) {
            return new ArticleChannelImpl(gateway, info);
        } else {
            return new ChannelImpl(gateway, info);
        }
    }

    @Override
    public Member fetchMember(String islandId, String dodoId) {
        Route route = API.V2.Member.info()
                .param("islandSourceId", islandId)
                .param("dodoSourceId", dodoId);
        JsonObject info = gateway.executeRequest(route).getAsJsonObject();
        info.addProperty("islandSourceId", islandId);
        return new MemberImpl(gateway, info);
    }

    @Override
    public String uploadImage(File imageFile) {
        Route route = API.V2.Resource.pictureUpload().param("file", imageFile);
        return this.gateway.executeRequest(route).getAsJsonObject().get("url").getAsString();
    }

    @Override
    public String updateMessage(String messageId, Message content) {
        Route route = API.V2.Channel.messageEdit()
                .param("messageId", messageId)
                .param("messageType", content.getType().getCode())
                .param("messageBody", content.get());
        return this.gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
    }

    @Override
    public List<HistoryMessage> getChannelMessages(Channel channel) {
        return this.historyManager.getChannelMessages(channel);
    }

    @Override
    public Optional<HistoryMessage> fetchMessage(String messageId) {
        return this.historyManager.fetchMessage(messageId);
    }

    @Override
    public void recordChannelMessage(String channelId, long timestamp, String messageId, Message message) {
        this.historyManager.recordChannelMessage(channelId,timestamp, messageId, message);
    }

    private void pkgReceive(JsonObject pkg) {
        JsonObject data = pkg.getAsJsonObject("data");
        if (Objects.equals(data.get("eventType").getAsString(), "2001")) {
            if (data.getAsJsonObject("eventBody").get("messageType").getAsInt() == 1) {
                try {
                    long timestamp = data.get("timestamp").getAsLong();
                    JsonObject eventJson = data.getAsJsonObject("eventBody");
                    String islandId = string(eventJson, "islandSourceId");
                    String channelId = string(eventJson, "channelId");
                    String dodoId = string(eventJson, "dodoSourceId");
                    String messageId = string(eventJson, "messageId");
                    Member member = this.fetchMember(islandId, dodoId);

                    MessageType type = MessageType.of(eventJson.get("messageType").getAsInt());
                    Message body = Message.parse(type, eventJson.getAsJsonObject("messageBody"));
                    MessageContext context;
                    if(StringUtils.isEmpty(islandId)){
                        context = new MessageContextImpl(timestamp, messageId, body,
                                member, this.fetchChannel(islandId, channelId), null);
                    }else{
                        context = new MessageContextImpl(timestamp, messageId, body,
                                member, this.fetchChannel(islandId, channelId), this.fetchIsland(islandId));
                    }

                    if (body != null) {
                        commandManager.execute(context, ((TextMessage) body).getContent());
                    }
                } catch (Exception ignored) {
                    // To prevent the event won't be fired
                }
            }
        }
        this.eventManager.callEvent(data);
    }

    private String string(JsonObject object, String key) {
        return object.get(key).getAsString();
    }
}
