package dns.model;
/**
 * Classe que representa o cabecalho DNS.
 */
public class DNSHeader {
    private final int id;         //identificacao do programa que gerou a query
    private final DNSFlags flags; //controla comportamento da query/resposta
  
    //Obs.: 16 bits ou 2 bytes para cada campo

    /**
     * Cria cabecalho DNS.
     * <p>
     * As flags sao inicializadas com valores padrao para uma query DNSFlags 
     * 
     * @param id
     *        Identificador da mensagem DNS (deve estar entre 0 e 65535)  
     */
    public DNSHeader(int id) {
        if(id < 0 || id > 0xFFFF) {
            throw new IllegalArgumentException("DNS ID deve estar entre 0 e 65535");
        }
        this.id = id;
        this.flags = new DNSFlags();
    }

    

    /** 
     * Metodo usado para mudar ou retornar as flags do header
    */
    public DNSFlags flags() {return this.flags;}
    public int getId()      {return this.id;}
}
