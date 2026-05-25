package dns.model;

import java.util.ArrayList;
import java.util.List;

import dns.model.enums.OperationCode;
import dns.model.enums.ReturnCode;

/**
 * Classe que cria a estrutura da mensagem DNS
 */
public class DNSMessage {
    //Conteudo do header====================================================================
    private final DNSHeader header;
    //Os valores de count serao representados pelo "List.size()" para evitar inconsistencia
    //======================================================================================
    //Conteudo do corpo/resto da mensagem===================================================
    private final List<DNSQuestion> questions;
    private final List<DNSResourceRecord> answers;
    private final List<DNSResourceRecord> authorities;
    private final List<DNSResourceRecord> additionals;
    //======================================================================================

    /**
     * Cria cabecalho DNS.
     * <p>
     * As flags sao inicializadas com valores padrao para uma query DNSFlags.
     * 
     * @param id
     *        Identificador da mensagem DNS (deve estar entre 0 e 65535, ou 0xFFFF).  
     * 
     * @param question
     *        Campo obrigatorio para qualquer mensagem DNS.
     */
    public DNSMessage(int id, DNSQuestion question) {
        header = new DNSHeader(id);

        questions = new ArrayList<DNSQuestion>();
        questions.add(question);

        answers = new ArrayList<DNSResourceRecord>();
        authorities = new ArrayList<DNSResourceRecord>();
        additionals = new ArrayList<DNSResourceRecord>();
    }

    public void addQuestion(DNSQuestion q) {
        questions.add(q);
    }

    public void addAnswer(DNSResourceRecord a) {
        answers.add(a);
    }

    public void addAuthorities(DNSResourceRecord a) {
        authorities.add(a);
    }

    public void addAdditionals(DNSResourceRecord a) {
        additionals.add(a);
    }
      
    public void addFlags(DNSFlags f) {
        header.flags().query(f.getQr())
                      .operationCode(f.getOpcode())
                      .authoritativeAnswer(f.getAa())
                      .truncated(f.getTc())
                      .recursionDesired(f.getRd())
                      .recursionAvailable(f.getRa())
                      .returnCode(f.getRCode());
    }

    public DNSHeader getHeader()                    {return this.header;}
    public List<DNSQuestion> getQuestions()         {return this.questions;}
    public List<DNSResourceRecord> getAnswers()     {return this.answers;}
    public List<DNSResourceRecord> getAuthorities() {return this.authorities;}
    public List<DNSResourceRecord> getAdditionals() {return this.additionals;}

    //======================================================================================
    //toString de DNSMessage
    //======================================================================================
    private void appendQuestions(StringBuilder sb) {
        sb.append("QUESTION\n");
        sb.append("----------------------------------------------\n");
            if(questions.isEmpty()) {
                sb.append("<empty>\n\n");
                return;
            }

            for(DNSQuestion q : questions) {

                sb.append("QNAME:      ")
                  .append(q.getQName())
                  .append("\n");

                sb.append("QTYPE:      ")
                  .append(q.getQType())
                  .append("\n");

                sb.append("QCLASS:     ")
                  .append(q.getQClass())
                  .append("\n\n");
            }
    }

    private void appendRecords(StringBuilder sb, String section, List<DNSResourceRecord> records) {
        sb.append(section).append("\n");
        sb.append("----------------------------------------------\n");

        if (records.isEmpty()) {
            sb.append("<empty>\n\n");
            return;
        }

        for (DNSResourceRecord rr : records) {

            sb.append("NAME:       ")
              .append(rr.getName())
              .append("\n");

            sb.append("TYPE:       ")
              .append(rr.getType())
              .append("\n");

            sb.append("CLASS:      ")
              .append(rr.getClassRR())
              .append("\n");

            sb.append("TTL:        ")
              .append(rr.getTTL())
              .append("\n");

            sb.append("RDATA:      ")
              .append(rr.getRData())
              .append("\n\n");
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+---------------- DNS MESSAGE DECODED ----------------+\n");
        sb.append("HEADER\n");
        sb.append("----------------------------------------------\n");
        sb.append("ID:         ")
          .append(header.getId())
          .append("\n");

        sb.append("FLAGS:      ")
          .append(header.flags())
          .append("\n\n");

        sb.append("QDCOUNT:    ")
          .append(questions.size())
          .append("\n");

        sb.append("ANCOUNT:    ")
          .append(answers.size())
          .append("\n");

        sb.append("NSCOUNT:    ")
          .append(authorities.size())
          .append("\n");

        sb.append("ARCOUNT:    ")
          .append(additionals.size())
          .append("\n\n");

        appendQuestions(sb);
        appendRecords(sb, "ANSWER", answers);
        appendRecords(sb, "AUTHORITY", authorities);
        appendRecords(sb, "ADDITIONAL", additionals);
        sb.append("+-----------------------------------------------------+\n");

        return sb.toString();
    }
}