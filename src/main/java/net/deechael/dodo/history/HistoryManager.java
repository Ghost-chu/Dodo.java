package net.deechael.dodo.history;

import net.deechael.dodo.api.Channel;
import net.deechael.dodo.content.Message;

import java.util.List;
import java.util.Optional;

public interface HistoryManager {
    void recordChannelMessage(String channelId, long timestamp, String messageId, Message message);
    List<HistoryMessage> getChannelMessages(Channel channel);
    Optional<HistoryMessage> fetchMessage(String messageId);
}
