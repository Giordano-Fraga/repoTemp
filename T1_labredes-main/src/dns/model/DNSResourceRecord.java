package dns.model;

import dns.model.enums.DNSClass;
import dns.model.enums.DNSType;

public class DNSResourceRecord {
    private final String name;      //domain name que o RR pertence
    private final DNSType type;     //tipo do RR
    private final DNSClass classRR; //classe do dado em RData
    private final long TTL;         //intervalo de tempo que o RR pode ficar em cache antes de ser descartado

    //"rdLength" eh definido somente na codificacao da mensagem, pois ele so funciona quando se trata de bytes
    private int rdLength;           //tamanho do campo rData
    private final String rData;     //tamanho variado, definido em classRR e type
    /*
        Ex: 
        type = A 
        class = IN 
        rData eh um campo de 4 octetos para ARPA Internet address 
        e tem "rdLength" de 4 bytes devido ao IPv4
     */

    /**
     * Cria um Resource Record DNS, podendo ser:
     * 
     * <li> Answer
     * <li> Authority
     * <li> Additional
     * 
     * @param name Nome do dominio ao qual o RR pertence
     * @param type Tipo de RR, como "A", "CNAME", etc
     * @param classRR Somente usar "DNSClass.IN" por enquanto
     * @param TTL Tempo, em seg, de permanecer o RR em cache
     * @param rData Conteudo do RR, depende do tipo informado em {@code type}
     */
    public DNSResourceRecord(String name, DNSType type, DNSClass classRR, long TTL, String rData) {
        this.name = name;
        this.type = type;
        this.classRR = classRR;
        this.TTL = TTL;
        this.rData = rData;
    }
    
    public String getName()         {return this.name;}
    public DNSType getType()        {return this.type;}
    public DNSClass getClassRR()    {return this.classRR;}
    public long getTTL()            {return this.TTL;}
    public int getRdLength()        {return this.rdLength;}
    public String getRData()        {return this.rData;}
}
