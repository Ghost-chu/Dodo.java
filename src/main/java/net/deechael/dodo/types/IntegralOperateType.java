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
        switch (code) {
            case 1:
                return DEPOSIT;
            case 2:
                return WITHDRAW;
            default:
                return WITHDRAW;
        }
    }

}
