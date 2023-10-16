package net.deechael.dodo.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.deechael.dodo.api.Channel;
import net.deechael.dodo.history.HistoryManager;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.history.HistoryMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistoryManagerImpl implements HistoryManager {
    private final Cache<String, HistoryMessage> messageCache = CacheBuilder.newBuilder()
            .maximumSize(Integer.parseUnsignedInt(System.getProperty("dodo.java.max-cache-length")))
            .build();

    @Override
    public void recordChannelMessage(String channelId, long timestamp, String messageId, Message message) {
        messageCache.put(messageId, new HistoryMessage(channelId, timestamp, message));
    }

    @Override
    public List<HistoryMessage> getChannelMessages(Channel channel) {
        return messageCache.asMap().values()
                .stream()
                .filter(channelMessage -> channelMessage.getChannelId().equals(channel.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<HistoryMessage> fetchMessage(String messageId) {
        return Optional.ofNullable(messageCache.getIfPresent(messageId));
    }

}
