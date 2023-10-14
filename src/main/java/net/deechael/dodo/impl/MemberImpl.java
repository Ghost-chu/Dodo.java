package net.deechael.dodo.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.deechael.dodo.API;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.api.Role;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.types.IntegralOperateType;
import net.deechael.dodo.types.UserOnlineDeviceType;
import net.deechael.dodo.types.UserOnlineStatusType;
import net.deechael.dodo.types.UserSexType;

import java.util.ArrayList;
import java.util.List;

public class MemberImpl implements Member {

    private final Gateway gateway;

    @Getter
    private final String id;
    @Getter
    private final String islandId;
    @Getter
    private final String nickname;
    @Getter
    private final String personalNickname;
    @Getter
    private final String avatarUrl;
    @Getter
    private final String joinTime;
    @Getter
    private final UserSexType sex;
    @Getter
    private final int level;
    @Getter
    private final boolean bot;
    @Getter
    private final UserOnlineDeviceType onlineDevice;
    @Getter
    private final UserOnlineStatusType onlineStatus;

    public MemberImpl(Gateway gateway, JsonObject info) {
        this.gateway = gateway;
        this.id = info.get("dodoSourceId").getAsString();
        this.islandId = info.get("islandSourceId").getAsString();
        this.nickname = info.get("nickName").getAsString();
        this.personalNickname = info.get("personalNickName").getAsString();
        this.avatarUrl = info.get("avatarUrl").getAsString();
        this.joinTime = info.get("joinTime").getAsString();
        this.sex = UserSexType.of(info.get("sex").getAsInt());
        this.level = info.get("level").getAsInt();
        this.bot = info.get("isBot").getAsInt() == 1;
        this.onlineDevice = UserOnlineDeviceType.of(info.get("onlineDevice").getAsInt());
        this.onlineStatus = UserOnlineStatusType.of(info.get("onlineStatus").getAsInt());
    }

    @Override
    public List<Role> getRoles() {
        Route route = API.V2.Member.roleList()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId());
        List<Role> roles = new ArrayList<>();
        try {
            JsonArray array = gateway.executeRequest(route).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                object.addProperty("islandSourceId", getIslandId());
                roles.add(new RoleImpl(gateway, object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public void setNickname(String nickname) {
        Route route = API.V2.Member.nicknameEdit()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("nickName", nickname);
        gateway.executeRequest(route);
    }

    @Override
    public void mute(int seconds) {
        Route route = API.V2.Member.muteAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("duration", seconds);
        gateway.executeRequest(route);
    }

    @Override
    public void mute(int seconds, String reason) {
        Route route = API.V2.Member.muteAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("duration", seconds)
                .param("reason", reason);
        gateway.executeRequest(route);
    }

    @Override
    public void unmute() {
        Route route = API.V2.Member.muteRemove()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId());
        gateway.executeRequest(route);
    }

    @Override
    public void ban() {
        Route route = API.V2.Member.banAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId());
        gateway.executeRequest(route);
    }

    @Override
    public void ban(String reason) {
        Route route = API.V2.Member.banAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("reason", reason);
        gateway.executeRequest(route);
    }

    @Override
    public void banNotice(String channelId) {
        Route route = API.V2.Member.banAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("noticeChannelId", channelId);
        gateway.executeRequest(route);
    }

    @Override
    public void banNotice(String channelId, String reason) {
        Route route = API.V2.Member.banAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("noticeChannelId", channelId)
                .param("reason", reason);
        gateway.executeRequest(route);
    }

    @Override
    public void unban() {
        Route route = API.V2.Member.banRemove()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId());
        gateway.executeRequest(route);
    }

    @Override
    public void send(Message content) {
        Route route = API.V2.Personal.messageSend()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("messageType", content.getType().getCode())
                .param("messageBody", content.get());
        gateway.executeRequest(route);
    }

    @Override
    public void addRole(String roleId) {
        Route route = API.V2.Role.memberAdd()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("roleId", roleId);
        gateway.executeRequest(route);
    }

    @Override
    public void removeRole(String roleId) {
        Route route = API.V2.Role.memberRemove()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("roleId", roleId);
        gateway.executeRequest(route);
    }

    @Override
    public long getIntegral() {
        Route route = API.V2.Integral.getIntegralInfo()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId());
        return gateway.executeRequest(route).getAsJsonObject().get("integralBalance").getAsLong();
    }

    @Override
    public void editIntegral(IntegralOperateType operateType, long integral) {
        Route route = API.V2.Integral.getIntegralInfo()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", getId())
                .param("operateType", operateType.getCode())
                .param("integral", integral);
        gateway.executeRequest(route);
    }
}
