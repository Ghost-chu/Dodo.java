package net.deechael.dodo.types;

public enum IntegralOperateType {

    DEPOSIT(1),
    WITHDRAW(2);

    private final int code;

    IntegralOperateType(int status) {
        this.code = status;
    }

    public int getCode() {
        return code;
    }

    public static IntegralOperateType of(int code) {
        return switch (code) {
            case 1 -> DEPOSIT;
            case 2 -> WITHDRAW;
            default -> WITHDRAW;
        };
    }

}
