package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.deechael.dodo.API;
import net.deechael.dodo.api.Channel;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.types.ChannelType;

public class ChannelImpl implements Channel {

    protected final Gateway gateway;

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final ChannelType type;
    @Getter
    private final String islandId;
    private final boolean isDefault;
    @Getter
    private final String groupId;
    @Getter
    private final String groupName;

    public ChannelImpl(Gateway gateway, JsonObject info) {
        this.gateway = gateway;
        this.id = info.get("channelId").getAsString();
        this.name = info.get("channelName").getAsString();
        this.type = ChannelType.of(info.get("channelType").getAsInt());
        this.islandId = info.get("islandSourceId").getAsString();
        this.isDefault = info.get("defaultFlag").getAsInt() == 1;
        this.groupId = info.get("groupId").getAsString();
        this.groupName = info.get("groupName").getAsString();
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setName(String name) {
        Route route = API.V2.Channel.edit()
                .param("islandSourceId", getIslandId())
                .param("channelId", getId())
                .param("channelName", name);
        gateway.executeRequest(route);
    }

    @Override
    public void remove() {
        Route route = API.V2.Channel.remove()
                .param("islandSourceId", getIslandId())
                .param("channelId", getId());
        gateway.executeRequest(route);
    }

}
