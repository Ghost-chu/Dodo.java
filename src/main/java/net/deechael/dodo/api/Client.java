package net.deechael.dodo.api;

import net.deechael.dodo.command.DodoCommand;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.event.Listener;
import net.deechael.dodo.history.HistoryMessage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface Client {

    void start();

    boolean isStart();

    void addEventListener(Listener listener);

    void unregisterEventListener(Listener listener);

    void registerCommand(DodoCommand command);

    Island fetchIsland(String islandId);

    Channel fetchChannel(String islandId, String channelId);

    Member fetchMember(String islandId, String dodoId);

    String uploadImage(File imageFile);

    String updateMessage(String messageId, Message content);

    List<HistoryMessage> getChannelMessages(Channel channel);
    Optional<HistoryMessage> fetchMessage(String messageId);

}
