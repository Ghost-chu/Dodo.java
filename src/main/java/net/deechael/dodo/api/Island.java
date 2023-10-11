package net.deechael.dodo.api;

import net.deechael.dodo.types.ChannelType;

import java.util.List;

public interface Island {

    String getId();
    String getName();

    String getCoverUrl();

    int getMemberCount();

    int getOnlineMemberCount();

    String getDescription();

    String getDefaultChannelId();

    String getSystemChannelId();

    List<Member> getMembers();

    List<String> getMuteList();

    List<String> getBanList();

    List<Channel> getChannels();

    List<Role> getRoles();

    String createRole(String name);

    String createChannel(String name, ChannelType type);

}
