package net.deechael.dodo.event;

import com.google.gson.JsonObject;
import net.deechael.dodo.api.*;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.event.channel.*;
import net.deechael.dodo.event.channel.ChannelMessageEvent.Reference;
import net.deechael.dodo.event.channel.MessageReactionEvent.ReactionType;
import net.deechael.dodo.event.member.MemberJoinEvent;
import net.deechael.dodo.event.member.MemberLeaveEvent;
import net.deechael.dodo.event.member.MemberLeaveEvent.LeaveType;
import net.deechael.dodo.event.personal.PersonalMessageEvent;
import net.deechael.dodo.impl.MessageContextImpl;
import net.deechael.dodo.types.MessageType;
import net.deechael.dodo.types.UserSexType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EventManager {

    private final Client client;

    private final Map<Class<?>, List<Entry<Method, Object>>> handlers = new HashMap<>();

    private final static List<Class<?>> EVENT_CLASSES = Arrays.asList(
            ChannelMessageEvent.class, MessageReactionEvent.class, CardMessageButtonClickEvent.class,
            CardMessageFormSubmitEvent.class, CardMessageListSubmitEvent.class, MemberJoinEvent.class,
            MemberLeaveEvent.class, PersonalMessageEvent.class
    );

    public EventManager(Client client) {
        this.client = client;
    }

    public void addListener(Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()))
                continue;
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null)
                continue;
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length > 1)
                continue;
            Class<?> eventType = parameters[0];
            if (!EVENT_CLASSES.contains(eventType))
                continue;
            if (!handlers.containsKey(eventType))
                handlers.put(eventType, new ArrayList<>());
            if (Modifier.isStatic(method.getModifiers())) {
                handlers.get(eventType).add(new SimpleEntry<>(method, null));
            } else {
                handlers.get(eventType).add(new SimpleEntry<>(method, listener));
            }
        }
    }

    public void unregisterListener(Listener listener) {
        Class<?> listenerClass = listener.getClass();
        for (Class<?> keyClass : handlers.keySet()) {
            List<Entry<Method, Object>> found = handlers.get(keyClass).stream().filter(entry -> entry.getKey().getDeclaringClass() == listenerClass).toList();
            handlers.get(keyClass).removeAll(found);
        }
    }

    public void callEvent(JsonObject eventJson) {
        String id = string(eventJson, "eventId");
        String eventType = string(eventJson, "eventType");
        long timestamp = getLong(eventJson, "timestamp");
        eventJson = eventJson.getAsJsonObject("eventBody");
        if (Objects.equals(eventType, "2001")) {
            if (!handlers.containsKey(ChannelMessageEvent.class)) {
                return;
            }
            String islandId = string(eventJson, "islandSourceId");
            String channelId = string(eventJson, "channelId");
            String dodoId = string(eventJson, "dodoSourceId");
            String messageId = string(eventJson, "messageId");
            Member member = client.fetchMember(islandId, dodoId);
            // Reference
            JsonObject refObject = eventJson.getAsJsonObject("reference");
            Reference reference = null;
            if(refObject != null && !refObject.isJsonNull()) {
                reference = new Reference(string(refObject, "messageId"),
                        string(refObject, "dodoSourceId"),
                        string(refObject, "nickName"));
            }
            MessageType type = MessageType.of(integer(eventJson, "messageType"));
            Message body = Message.parse(type, eventJson.getAsJsonObject("messageBody"));
            MessageContext context = new MessageContextImpl(timestamp, messageId, body,
                    member, client.fetchChannel(islandId, channelId), client.fetchIsland(islandId));
            ChannelMessageEvent event = new ChannelMessageEvent(id, timestamp, context, islandId,
                    channelId, dodoId, messageId, member, reference, type, body);
            fireEvent(ChannelMessageEvent.class, event);
        } else if (Objects.equals(eventType, "3001")) {
            if (!handlers.containsKey(MessageReactionEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String channelId = string(eventJson, "channelId");
            String dodoId = string(eventJson, "dodoSourceId");
            String messageId = string(eventJson, "messageId");
            Member member = client.fetchMember(islandId, dodoId);
            String targetId = string(eventJson.getAsJsonObject("reactionTarget"), "id");
            String emojiId = string(eventJson.getAsJsonObject("reactionEmoji"), "id");
            ReactionType type = integer(eventJson, "reactionType") == 1 ? ReactionType.ADD : ReactionType.REMOVE;
            MessageReactionEvent event = new MessageReactionEvent(id, timestamp, islandId, channelId,
                    dodoId, messageId, member, targetId, emojiId, type);
            fireEvent(MessageReactionEvent.class, event);
        } else if (Objects.equals(eventType, "3002")) {
            if (!handlers.containsKey(CardMessageButtonClickEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String channelId = string(eventJson, "channelId");
            String dodoId = string(eventJson, "dodoSourceId");
            String messageId = string(eventJson, "messageId");
            Member member = client.fetchMember(islandId, dodoId);
            String interactCustomId = eventJson.has("interactCustomId") ? string(eventJson, "interactCustomId") : "";
            String value = string(eventJson.getAsJsonObject("value"), "id");
            CardMessageButtonClickEvent event = new CardMessageButtonClickEvent(id, timestamp, islandId, channelId,
                    dodoId, messageId, member, interactCustomId, value);
            fireEvent(CardMessageButtonClickEvent.class, event);
        } else if (Objects.equals(eventType, "3003")) {
            if (!handlers.containsKey(CardMessageFormSubmitEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String channelId = string(eventJson, "channelId");
            String dodoId = string(eventJson, "dodoSourceId");
            String messageId = string(eventJson, "messageId");
            Member member = client.fetchMember(islandId, dodoId);
            String interactCustomId = eventJson.has("interactCustomId") ? string(eventJson, "interactCustomId") : "";
            CardMessageFormSubmitEvent event = new CardMessageFormSubmitEvent(id, timestamp, islandId, channelId,
                    dodoId, messageId, member, interactCustomId, eventJson.getAsJsonArray("formData"));
            fireEvent(CardMessageFormSubmitEvent.class, event);
        } else if (Objects.equals(eventType, "3004")) {
            if (!handlers.containsKey(CardMessageListSubmitEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String channelId = string(eventJson, "channelId");
            String dodoId = string(eventJson, "dodoSourceId");
            String messageId = string(eventJson, "messageId");
            Member member = client.fetchMember(islandId, dodoId);
            String interactCustomId = eventJson.has("interactCustomId") ? string(eventJson, "interactCustomId") : "";
            CardMessageListSubmitEvent event = new CardMessageListSubmitEvent(id, timestamp, islandId, channelId,
                    dodoId, messageId, member, interactCustomId, eventJson.getAsJsonArray("listData"));
            fireEvent(CardMessageListSubmitEvent.class, event);
        } else if (Objects.equals(eventType, "4001")) {
            if (!handlers.containsKey(MemberJoinEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String dodoId = string(eventJson, "dodoSourceId");
            Member member = client.fetchMember(islandId, dodoId);
            String modifyTime = string(eventJson, "modifyTime");
            MemberJoinEvent event = new MemberJoinEvent(id, timestamp, member, islandId, dodoId, modifyTime);
            fireEvent(MemberJoinEvent.class, event);
        } else if (Objects.equals(eventType, "4002")) {
            if (!handlers.containsKey(MemberLeaveEvent.class))
                return;
            String islandId = string(eventJson, "islandSourceId");
            String dodoId = string(eventJson, "dodoSourceId");
            Member member = client.fetchMember(islandId, dodoId);
            LeaveType leaveType = integer(eventJson, "leaveType") == 1 ? LeaveType.SELF : LeaveType.KICKED;
            String operator = string(eventJson, "operateDoDoId");
            String modifyTime = string(eventJson, "modifyTime");
            MemberLeaveEvent event = new MemberLeaveEvent(id, timestamp, member, islandId, dodoId, leaveType, operator, modifyTime);
            fireEvent(MemberLeaveEvent.class, event);
        } else if (Objects.equals(eventType, "1001")) {
            if (!handlers.containsKey(PersonalMessageEvent.class))
                return;
            String dodoId = string(eventJson, "dodoSourceId");
            //Personal
            JsonObject personalObject = eventJson.getAsJsonObject("personal");
            String nickname = string(personalObject, "nickName");
            String avatarUrl = string(personalObject, "avatarUrl");
            UserSexType sex = UserSexType.of(integer(personalObject, "sex"));

            String messageId = string(eventJson, "messageId");
            MessageType type = MessageType.of(integer(eventJson, "messageType"));
            Message body = Message.parse(type, eventJson.getAsJsonObject("messageBody"));

            PersonalMessageEvent event = new PersonalMessageEvent(id, timestamp, dodoId, nickname, avatarUrl, sex,
                    messageId, type, body);
            fireEvent(PersonalMessageEvent.class, event);
        } else {
            // TODO: Unknown Event
        }
    }

    public void fireEvent(Class<? extends Event> eventClass, Event event) {
        if (!handlers.containsKey(eventClass))
            return;
        for (Entry<Method, Object> entry : handlers.get(eventClass)) {
            try {
                entry.getKey().invoke(entry.getValue(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private String string(JsonObject object, String key) {
        return object.get(key).getAsString();
    }

    private int integer(JsonObject object, String key) {
        return object.get(key).getAsInt();
    }

    private long getLong(JsonObject object, String key) {
        return object.get(key).getAsLong();
    }

}
