package dns.codec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import dns.model.*;

/**
 * Responsavel por codificar a classe abstrata DNSMessage em uma sequencia de bytes reais. Codifica somente mensagens de consulta/query
 * <p>
 * Usar metodo {@code encode} antes de usar {@code DatagramPacket}
 */
public class DNSEncoder {

    
    /**
     * Codifica objeto {@code DNSMessage} em sequencia {@code byte[]}
     * @param msg 
     *        Objeto {@code DNSMessage} a ser codificado
     * 
     * @return byte[] do objeto {@code DNSMessage} codificado
     *         
     */
    public static byte[] encode(DNSMessage msg) {
        ByteBuffer buffer = ByteBuffer.allocate(512); //assumindo tamanho classico de 512 bytes

        encodeHeader(buffer, msg);
        encodeQuestions(buffer, msg.getQuestions());

        return Arrays.copyOf(buffer.array(), buffer.position()); //evita enviar todos os 512 bytes, caso nao ocupados
    }

    private static void encodeHeader(ByteBuffer b, DNSMessage msg) {
        DNSHeader h = msg.getHeader();
        //Conteudo========================================
        b.putShort((short) h.getId());
        b.putShort(h.flags().toShort());
        //================================================

        //Counts==========================================
        b.putShort((short) msg.getQuestions().size());
        b.putShort((short) msg.getAnswers().size());
        b.putShort((short) msg.getAuthorities().size());
        b.putShort((short) msg.getAdditionals().size());
        //================================================
    }

    private static void encodeQuestions(ByteBuffer b, List<DNSQuestion> qList) {
        for(DNSQuestion q : qList) {
            encodeDomain(b, q.getQName());      //domain name
            b.putShort(q.getQType().value());   //type
            b.putShort(q.getQClass().value());  //class
        }
    }
    private static void encodeDomain(ByteBuffer b, String domain) {
        String[] labels = domain.split("\\."); //separa o dominio por "."

        for(String label : labels) {        //para cada parte do dominio
            if(label.length() > 63) {throw new IllegalArgumentException("Tamanho da string deve ser menor que 63");}

            b.put((byte) label.length());                       //coloca tamanho da string em bytes
            b.put(label.getBytes(StandardCharsets.US_ASCII));   //coloca a string em bytes
        }                                                       //cria algo como "[06]google[03]com[00]"

        b.put((byte) 0); //fim do nome de domínio
    }
}
