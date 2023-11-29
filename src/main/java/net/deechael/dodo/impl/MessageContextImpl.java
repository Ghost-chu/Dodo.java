package net.deechael.dodo.impl;

import lombok.Getter;
import net.deechael.dodo.api.*;
import net.deechael.dodo.content.Message;
import org.jetbrains.annotations.Nullable;

public class MessageContextImpl implements MessageContext {

    @Getter
    private final long timestamp;
    @Getter
    private final String messageId;
    @Getter
    private final Message content;
    @Getter
    private final Member author;
    @Getter
    private final Channel channel;
    @Getter
    @Nullable
    private final Island island;


    public MessageContextImpl(long timestamp,
                              String messageId,
                              Message content,
                              Member author,
                              Channel channel,
                              @Nullable Island island
    ) {
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.content = content;
        this.author = author;
        this.channel = channel;
        this.island = island;
    }

    @Override
    public void reply(Message content) {
        ((TextChannel) channel).send(content, this.messageId);
    }

    @Override
    public void addReaction(String emoji) {
        ((TextChannel) content).addReaction(this.messageId, emoji);
    }

    @Override
    public void removeReaction(String emoji) {
        ((TextChannel) content).removeReaction(this.messageId, emoji);
    }

    @Override
    public void removeReaction(String emoji, String dodoId) {
        ((TextChannel) content).removeReaction(this.messageId, emoji, dodoId);
    }

}
