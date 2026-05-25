package dns.model.enums;

public enum DNSClass {
    IN(1);

    private final int value;

    DNSClass(int value) {
        this.value = value;
    }

    public short value() {
        return (short) value;
    }

    public static DNSClass fromValue(int value) {
        for (DNSClass classs : values()) {
            if (classs.value == value) {return classs;}
        }

        throw new IllegalArgumentException(
            "Classe DNS nao suportada: " + value
        );
    }
}