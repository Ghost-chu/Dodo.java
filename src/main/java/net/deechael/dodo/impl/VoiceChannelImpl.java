package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import net.deechael.dodo.API;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.api.VoiceChannel;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.types.VoiceChannelOperationType;

public class VoiceChannelImpl extends ChannelImpl implements VoiceChannel {

    public VoiceChannelImpl(Gateway gateway, JsonObject info) {
        super(gateway, info);
    }

    @Override
    public void mute(Member member) {
        operate(member, VoiceChannelOperationType.MUTE);
    }

    @Override
    public void kick(Member member) {
        operate(member, VoiceChannelOperationType.KICK);
    }

    @Override
    public void microphoneOn(Member member) {
        operate(member, VoiceChannelOperationType.ON);
    }

    @Override
    public void microphoneOff(Member member) {
        operate(member, VoiceChannelOperationType.OFF);
    }

    @Override
    public void move(Member member) {
        gateway.executeRequest(API.V2.Channel.voiceMemberMove()
                .param("islandSourceId", getIslandId())
                .param("dodoSourceId", member.getId())
                .param("channelId", getId()));
    }

    private void operate(Member member, VoiceChannelOperationType operationType) {
        gateway.executeRequest(API.V2.Channel.voiceMemberEdit()
                .param("channelId", this.getId())
                .param("dodoSourceId", member.getId())
                .param("operateType", operationType.getCode()));
    }

}
