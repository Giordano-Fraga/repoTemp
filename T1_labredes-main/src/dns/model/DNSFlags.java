package dns.model;

import dns.model.enums.OperationCode;
import dns.model.enums.ReturnCode;

/**
 * Classe que constroi o campo "flags" de DNSHeaderField <p>
 * Obs.: inicializa com valores padrao para busca, mudar somente se necessario
 */
public class DNSFlags {
    //Opcoes em "Flags"=================================================================================================================
    //Em ordem de bit mais significativo ao menos significativo
    private boolean qr = false;         //QR        = 1 bit  (diz se eh resposta ou query)
    private OperationCode opcode = OperationCode.NORMAL_QUERY; //Opcode = 4 bits (especifica tipo de query)     
    private boolean aa = false;         //AA        = 1 bit  (resposta autoritativa (Authoritative Answer))
    private boolean tc = false;         //TC        = 1 bit  (TrunCamento)
    private boolean rd = true;          //RD        = 1 bit  (diz para o name server fazer todas as queries necessarias para resolver o domain name)
    private boolean ra = false;         //RA        = 1 bit  (diz se query recursiva eh suportada no name server)
    private int z = 0;                  //Z         = 3 bits (reservado para uso futuro)
    private ReturnCode rCode = ReturnCode.NO_ERROR; //rCode = 4 bits (codigo de resposta)
    //==================================================================================================================================

    //false -> query
    //true  -> resposta
    public DNSFlags query(boolean value) {
        this.qr = value;
        return this;
    }

    public DNSFlags operationCode(OperationCode value) {
        this.opcode = value;
        return this;
    }

    public DNSFlags authoritativeAnswer(boolean value) {
        this.aa = value;
        return this;
    }

    public DNSFlags truncated(boolean value) {
        this.tc = value;
        return this;
    }

    public DNSFlags recursionDesired(boolean value) {
        this.rd = value;
        return this;
    }

    public DNSFlags recursionAvailable(boolean value) {
        this.ra = value;
        return this;
    }

    public DNSFlags z(int value) {
        this.z = value;
        return this;
    }

    public DNSFlags returnCode(ReturnCode value) {
        this.rCode = value;
        return this;
    }


    public boolean getQr()              {return this.qr;}
    public OperationCode getOpcode()    {return this.opcode;}
    public boolean getAa()              {return this.aa;}
    public boolean getTc()              {return this.tc;}
    public boolean getRd()              {return this.rd;}
    public boolean getRa()              {return this.ra;}
    public int getZ()                   {return this.z;}
    public ReturnCode getRCode()        {return this.rCode;}

    /**
     * Transforma o objeto DNSFlag em campo
     * @return short (2 bytes)
     */
    public short toShort() {
        int flags = 0;

        // "|=" eh um OR e basicamente soma o valor gerado com "flags"
        flags |= (qr ? 1 : 0) << 15;
        flags |= (opcode.value() & 0xF) << 11;
        flags |= (aa ? 1 : 0) << 10;
        flags |= (tc ? 1 : 0) << 9;
        flags |= (rd ? 1 : 0) << 8;
        flags |= (ra ? 1 : 0) << 7;
        flags |= (z & 0x7) << 4;
        flags |= (rCode.value() & 0xF);

        return (short) flags;
    }

    @Override
    public String toString() {
        return "QR = " + qr + 
               "\n            AA = " + aa + 
               "\n            TC = " + tc + 
               "\n            RD = " + rd + 
               "\n            RA = " + ra + 
               "\n            rCode = " + rCode;
    }
}
