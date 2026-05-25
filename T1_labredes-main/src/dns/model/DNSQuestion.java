package dns.model;

import dns.model.enums.DNSClass;
import dns.model.enums.DNSType;

/**
 * Classe que cria o campo questions DNS
 */
public class DNSQuestion {
    private final String qName;     //nome do dominio sendo questionado
    private final DNSType qType;    //tipo da query
    private final DNSClass qClass;  //classe da query

    /**
     * Contructor basico de DNSQuestion
     * @param qName dominio do site "google.com" por exemplo
     * @param qType tipo da query a ser feita como "A" ou "CNAME"
     * @param qClass provavelmente so vai ser usado "QueryClass.IN"
     */
    public DNSQuestion(String qName, DNSType qType, DNSClass qClass) {
            this.qName = qName;
            this.qType = qType;
            this.qClass = qClass;
    }

    public String getQName()    {return qName;}
    public DNSType getQType()   {return qType;}
    public DNSClass getQClass() {return qClass;}
}
