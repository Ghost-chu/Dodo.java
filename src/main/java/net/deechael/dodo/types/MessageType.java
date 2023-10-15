package net.deechael.dodo.types;

import lombok.Getter;

public enum MessageType {

    TEXT(1, "text"),
    IMAGE(2, "image"),
    VIDEO(3, "video"),
    SHARE(4, "share"),
    FILE(5, "file"),
    CARD(6, "card"),
    UNKNOWN(-1, "unknown");

    @Getter
    private final int code;
    @Getter
    private final String name;

    MessageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MessageType of(int code) {
        switch (code) {
            case 1:
                return TEXT;
            case 2:
                return IMAGE;
            case 3:
                return VIDEO;
            case 6:
                return CARD;
            default:
                return UNKNOWN;
        }
    }

}
