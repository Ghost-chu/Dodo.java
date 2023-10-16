package net.deechael.dodo.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.deechael.dodo.content.Message;

@AllArgsConstructor
@Data
public class HistoryMessage {
    private String channelId;
    private long timestamp;
    private Message message;
}
