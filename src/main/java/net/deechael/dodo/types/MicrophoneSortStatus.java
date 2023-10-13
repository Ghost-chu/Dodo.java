package net.deechael.dodo.types;

public enum MicrophoneSortStatus {

    OFF(1),
    REQUESTING(2),
    ON(3);

    private final int code;

    MicrophoneSortStatus(int status) {
        this.code = status;
    }

    public int getCode() {
        return code;
    }

    public static MicrophoneSortStatus of(int code) {
        return switch (code) {
            case 2 -> REQUESTING;
            case 3 -> ON;
            default -> OFF;
        };
    }

}
