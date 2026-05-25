package dns.codec;

import java.nio.ByteBuffer;
import java.util.Arrays;

import dns.model.*;
import dns.model.enums.*;

/**
 * Responsavel por decodificar uma sequencia de bytes na classe abstrata DNSMessage.
 * <p>
 * Usar metodo {@code encode} antes de usar {@code DatagramPacket}
 */
public class DNSDecoder {

    /**
     * Decodifica mensagem em {@code bytes[]} para o objeto {@code DNSMessage}
     * @param dnsMsg {@code byte[]} que sera decodificado em um objeto {@code DNSMessage}
     */
    public static DNSMessage decode(byte[] dnsMsg) {
        ByteBuffer buffer = ByteBuffer.wrap(dnsMsg);

        //Header===============================================
        int id = buffer.getShort();
        short flagsBytes = buffer.getShort();
        DNSFlags flags = decodeFlags(flagsBytes);

        int qdCount = buffer.getShort();
        int anCount = buffer.getShort();
        int nsCount = buffer.getShort();
        int arCount = buffer.getShort();
        //=====================================================

        //Question=============================================
        DNSQuestion questionResponse = decodeQuestion(buffer);

        DNSMessage msg = new DNSMessage(id, questionResponse); 
        msg.addFlags(flags);
        
        for(int i = 1; i < qdCount; i++) {
            questionResponse = decodeQuestion(buffer);
            msg.addQuestion(questionResponse);
        }
        //=====================================================

        //Answers==============================================
        for(int i = 0; i < anCount; i++) {
            msg.addAnswer(decodeRecord(buffer));
        }
        //=====================================================

        //Authorities==========================================
        for(int i = 0; i < nsCount; i++) {
            msg.addAuthorities(decodeRecord(buffer));
        }
        //=====================================================

        //Additionals==========================================
        for(int i = 0; i < arCount; i++) {
            msg.addAdditionals(decodeRecord(buffer));
        }
        //=====================================================

        return msg;
    }

    /**
     * Decodifica o campo flags do header
     * @param flags Campo que sera decodificado
     * @return {@code DNSFlags} com os dados dentro de {@code flags}
     */
    private static DNSFlags decodeFlags(short flags) {
        boolean qr = ((flags >> 15) & 0x1) == 1;
        int opcode = (flags >> 11) & 0xF;
        boolean aa = ((flags >> 10) & 0x1) == 1;
        boolean tc = ((flags >> 9) & 0x1) == 1;
        boolean rd = ((flags >> 8) & 0x1) == 1;
        boolean ra = ((flags >> 7) & 0x1) == 1;
        int rCode = flags & 0xF;

        return new DNSFlags().query(qr)
                             .operationCode(OperationCode.fromValue(opcode))
                             .authoritativeAnswer(aa)
                             .truncated(tc)
                             .recursionDesired(rd)
                             .recursionAvailable(ra)
                             .returnCode(ReturnCode.fromValue(rCode));
    }

    /**
     * Decodifica o campo question da mensagem DNS 
     * @param b Buffer que tem a mensagem DNS
     * @return {@code DNSQuestion} com os dados dentro do {@code ByteBuffer}
     */
    private static DNSQuestion decodeQuestion(ByteBuffer b) {
        String qName = decodeDomain(b);
        int qTypeValue = Short.toUnsignedInt(b.getShort()); //"toUnsignedInt" pois o valor de unsigned short nao existe
        DNSType qType = DNSType.fromValue(qTypeValue); //transforma o valor em enum
        int qClassValue = Short.toUnsignedInt(b.getShort());
        DNSClass qClass = DNSClass.fromValue(qClassValue);

        return new DNSQuestion(qName, qType, qClass);
    }
    private static String decodeDomain(ByteBuffer b) {
        StringBuilder domain = new StringBuilder();

        int jumpPosition = -1;

        while(true) {
            int length = Byte.toUnsignedInt(b.get()); //pega tamanho da label a seguir

            if(length == 0) {break;} //se eh zero, acabou o dominio

            //compression pointer
            if((length & 0xC0) == 0xC0) { //se os dois bits mais significativos forem 0xC0
                int next = Byte.toUnsignedInt(b.get());
                int pointer = ((length & 0x3F) << 8) | next;
                
                if(jumpPosition == -1) {
                    jumpPosition = b.position();
                }

                b.position(pointer);

                continue;
            }

            //cria array e pega label/string
            byte[] labelBytes = new byte[length];
            b.get(labelBytes);
            String label = new String(labelBytes);

            //se tiver nada, estamos num "."
            if(!domain.isEmpty()) {domain.append(".");}

            //termina fazendo append da string
            domain.append(label);
        }

        if(jumpPosition != -1) {
            b.position(jumpPosition);
        }

        return domain.toString();
    }

    /**
     * Decodifica o campo Resource Records da mensagem DNS
     * @param b Buffer que tem a mensagem DNS
     * @return {@code DNSResourceRecord} com os dados dentro do {@code ByteBuffer}
     */
    private static DNSResourceRecord decodeRecord(ByteBuffer b) {
        String name = decodeDomain(b);
        int typeValue = Short.toUnsignedInt(b.getShort());
        DNSType type = DNSType.fromValue(typeValue);
        int classValue = Short.toUnsignedInt(b.getShort());
        DNSClass dnsClass = DNSClass.fromValue(classValue);
        long ttl = Integer.toUnsignedLong(b.getInt());
        int rdLength = Short.toUnsignedInt(b.getShort());
        String rData = decodeRData(b, type, rdLength);

        return new DNSResourceRecord(name, type, dnsClass, ttl, rData);
    }
    private static String decodeRData(ByteBuffer b, DNSType t, int rdL) {
        //switch expression: forma mais moderna de usar o switch case em Java
        return switch(t) {
            case A -> decodeIPv4(b);

            case NS, CNAME -> decodeDomain(b);

            case SOA -> decodeSOA(b); 

            //se nao reconhecer, apenas joga os valores "crus" em bytes
            default -> {
                byte[] raw = new byte[rdL];
                b.get(raw);

                //"yield" eh usado em bloco com multiplas linhas (equivalente a um return)
                yield Arrays.toString(raw);
            }
        };
    }
    private static String decodeIPv4(ByteBuffer b) {
        StringBuilder ip = new StringBuilder();
        
        for (int i = 0; i < 4; i++) {
            int part = Byte.toUnsignedInt(b.get());
            ip.append(part);

            if(i < 3) {ip.append(".");}
        }

        return ip.toString();
    }
    private static String decodeSOA(ByteBuffer b) {
        String mName = decodeDomain(b);
        String rName = decodeDomain(b);
        long serial = Integer.toUnsignedLong(b.getInt());
        long refresh = Integer.toUnsignedLong(b.getInt());
        long retry = Integer.toUnsignedLong(b.getInt());
        long expire = Integer.toUnsignedLong(b.getInt());
        long minimum = Integer.toUnsignedLong(b.getInt());

        return "MNAME = " + mName + 
               "\n            RNAME = " + rName + 
               "\n            SERIAL = " + serial + 
               "\n            REFRESH = " + refresh + 
               "\n            RETRY = " + retry + 
               "\n            EXPIRE = " + expire +
               "\n            MINIMUM = " + minimum;
    }
}
