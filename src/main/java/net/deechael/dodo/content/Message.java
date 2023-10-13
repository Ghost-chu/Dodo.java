package net.deechael.dodo.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.deechael.dodo.types.MessageType;

public interface Message {

    JsonElement get();

    MessageType getType();

    static Message parse(MessageType type, String content) {
        return parse(type, new JsonParser().parse(content).getAsJsonObject());
    }

    static Message parse(MessageType type, JsonObject object) {
        if (type == MessageType.TEXT) {
            return new TextMessage(object.get("content").getAsString());
        } else if (type == MessageType.IMAGE) {
            String url = object.get("url").getAsString();
            int width = object.get("width").getAsInt();
            int height = object.get("height").getAsInt();
            int original = object.get("isOriginal").getAsInt();
            ImageMessage message = new ImageMessage(url, width, height);
            message.setOriginal(original == 1);
            return message;
        } else if (type == MessageType.VIDEO) {
            String url = object.get("url").getAsString();
            String coverUrl = object.get("coverUrl").getAsString();
            long duration = object.get("duration").getAsLong();
            long size = object.get("size").getAsLong();
            VideoMessage message = new VideoMessage(url);
            message.setCoverUrl(coverUrl);
            message.setDuration(duration);
            message.setSize(size);
            return message;
        } else if (type == MessageType.SHARE) {
            return new ShareMessage(object.get("jumpUrl").getAsString());
        } else if (type == MessageType.FILE) {
            String url = object.get("url").getAsString();
            String name = object.get("name").getAsString();
            long size = object.get("size").getAsLong();
            return new FileMessage(url, name, size);
        } else if (type == MessageType.CARD) {
            // TODO must be done because event system can receive card message now
        }
        return null;
    }

}
