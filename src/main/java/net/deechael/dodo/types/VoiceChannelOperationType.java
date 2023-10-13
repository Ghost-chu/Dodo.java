package net.deechael.dodo.types;

public enum VoiceChannelOperationType {

    OFF(0),
    ON(1),
    MUTE(2),
    KICK(3);

    private final int code;

    VoiceChannelOperationType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static VoiceChannelOperationType of(int code) {
        return switch (code) {
            case 1 -> ON;
            case 2 -> MUTE;
            case 3 -> KICK;
            default -> OFF;
        };
    }

}
