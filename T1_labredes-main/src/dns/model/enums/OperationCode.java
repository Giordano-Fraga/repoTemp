package dns.model.enums;

    /*  0 - query normal
        1 - query reverso
        2 - server status
        3 ate 15 - reservado para o futuro  */
public enum OperationCode {
    NORMAL_QUERY(0),
    
    REVERSE_QUERY(1),
    
    SERVER_STATUS(2);

    //qualquer valor acima de 2 eh "reservado para o futuro"

    private final int value;

    OperationCode(int value) {
        this.value = value;
    }

    public short value() {
        return (short) value;
    }

    public static OperationCode fromValue(int value) {
        for (OperationCode opcode : values()) {
            if (opcode.value == value) {return opcode;}
        }

        throw new IllegalArgumentException(
            "OPCode DNS nao suportado: " + value
        );
    }
}
