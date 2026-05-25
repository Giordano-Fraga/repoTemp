package dns.model.enums;

/**
 * Enum para o tipo de query
 */
public enum DNSType {
    /**
     * Endereco IPv4
     */
    A(1),

    /**
     * Namespace
     */
    NS(2),

    MD(3),

    MF(4),

    /**
     * Nome canonico
     */
    CNAME(5),

    SOA(6),

    WKS(11),

    PTR(12),

    HINFO(13),

    MINFOR(14),

    /**
     * Mail eXchanger
     */
    MX(15),

    /**
     * Endereco IPv6
     */
    AAAA(28);

    private final int value;

    DNSType(int value) {
        this.value = value;
    }

    public short value() {
        return (short) value;
    }

    public static DNSType fromValue(int value) {
        for (DNSType type : values()) {
            if (type.value == value) {return type;}
        }

        throw new IllegalArgumentException(
            "Tipo DNS nao suportada: " + value
        );
    }
}