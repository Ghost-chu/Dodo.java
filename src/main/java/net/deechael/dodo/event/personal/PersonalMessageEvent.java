package net.deechael.dodo.event.personal;

import lombok.Getter;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.event.Event;
import net.deechael.dodo.types.EventType;
import net.deechael.dodo.types.MessageType;
import net.deechael.dodo.types.UserSexType;

public class PersonalMessageEvent extends Event {
    @Getter
    private final String islandId;
    @Getter
    private final String dodoId;
    @Getter
    private final String nickname;
    @Getter
    private final String avatarUrl;
    @Getter
    private final UserSexType sex;
    @Getter
    private final Member member;
    @Getter
    private final String messageId;
    @Getter
    private final MessageType messageType;
    @Getter
    private final Message body;

    public PersonalMessageEvent(String id,
                                long timestamp,
                                String islandId,
                                String dodoId,
                                String nickname,
                                String avatarUrl,
                                UserSexType sex,
                                Member member,
                                String messageId,
                                MessageType messageType,
                                Message body) {
        super(id, EventType.PERSONAL_MESSAGE, timestamp);
        this.islandId = islandId;
        this.dodoId = dodoId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.sex = sex;
        this.member = member;
        this.messageId = messageId;
        this.messageType = messageType;
        this.body = body;
    }

}
