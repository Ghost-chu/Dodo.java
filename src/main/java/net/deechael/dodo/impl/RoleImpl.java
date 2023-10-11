package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.deechael.dodo.API;
import net.deechael.dodo.api.Role;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.network.Route;

public class RoleImpl implements Role {

    private final Gateway gateway;

    @Getter
    private final String islandId;
    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String color;
    @Getter
    private final int position;
    @Getter
    private final String permission;

    public RoleImpl(Gateway gateway, JsonObject info) {
        this.gateway = gateway;
        this.islandId = info.get("islandSourceId").getAsString();
        this.id = info.get("roleId").getAsString();
        this.name = info.get("roleName").getAsString();
        this.color = info.get("roleColor").getAsString();
        this.position = info.get("position").getAsInt();
        this.permission = info.get("permission").getAsString();
    }

    @Override
    public void setName(String name) {
        Route route = API.V2.Role.edit()
                .param("islandSourceId", getIslandId())
                .param("roleId", getId())
                .param("roleName", name);
        gateway.executeRequest(route);
    }

    @Override
    public void setColor(String color) {
        Route route = API.V2.Role.edit()
                .param("islandSourceId", getIslandId())
                .param("roleId", getId())
                .param("roleColor", color);
        gateway.executeRequest(route);
    }

    @Override
    public void setPosition(int position) {
        Route route = API.V2.Role.edit()
                .param("islandSourceId", getIslandId())
                .param("roleId", getId())
                .param("position", position);
        gateway.executeRequest(route);
    }

    @Override
    public void setPermission(String permission) {
        Route route = API.V2.Role.edit()
                .param("islandSourceId", getIslandId())
                .param("roleId", getId())
                .param("permission", permission);
        gateway.executeRequest(route);
    }

    @Override
    public void remove() {
        Route route = API.V2.Role.remove()
                .param("islandSourceId", getIslandId())
                .param("roleId", getId());
        gateway.executeRequest(route);
    }

}
