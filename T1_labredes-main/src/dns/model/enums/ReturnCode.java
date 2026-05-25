package dns.model.enums;

public enum ReturnCode {
    NO_ERROR(0),

    FORMAT_ERROR(1),

    SERVER_FAILURE(2),

    NAME_ERROR(3),

    NOT_IMPLEMENTED(4),

    REFUSED(5),

    YXDOMAIN(6),

    XRRSET(7),

    NOTAUTH(8),

    NOTZONE(9);

    private final int value;

    ReturnCode(int value) {
        this.value = value;
    }

    public short value() {
        return (short) value;
    }

    public static ReturnCode fromValue(int value) {
        for (ReturnCode code : values()) {
            if (code.value == value) {return code;}
        }

        throw new IllegalArgumentException(
            "Return Code DNS nao suportado: " + value
        );
    }
}
