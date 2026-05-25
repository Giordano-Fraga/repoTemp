package dns.network;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Usada para calcular taxa de perda, tempos minimos maximos e medios
 */
public class DNSQueryStats implements Comparable<DNSQueryStats>{
    private final List<Long> times;
    private int lostPackets;
    private String domain;

    public DNSQueryStats(String domain) {
        this.domain = domain;
        this.times = new LinkedList<>();
        this.lostPackets = 0;
    }

    /**
     * Armazena o tempo de uma requisicao. Enviar tempo apenas usando {@code System.nanoTime()}
     * @param timeMs Tempo em ms a ser armazenado
     */
    public void addSuccess(long timeMs) {
        times.add(timeMs);
    }

    /**
     * Adiciona packets perdidos
     */
    public void addLoss() {lostPackets++;}

    /**
     * Calcula media dos tempos armazenados
     * @return Media dos tempos armazenados
     */
    public double calcAvg() {
        if(times.isEmpty()) {return 0;}

        long sum = 0;

        for(long t : times) {
            sum += t;
        }

        return (double) sum/times.size();
    }

    /**
     * Pega o menor tempo armazenado
     * @return O menor tempo armazenado
     */
    public long min() {
        if(times.isEmpty()) {return 0;}
        return Collections.min(times);
    }

    /**
     * Pega o maior tempo armazenado
     * @return O maior tempo armazenado
     */
    public long max() {
        if(times.isEmpty()) {return 0;}
        return Collections.max(times);}

    /**
     * Calcula media de perda de packets
     * 
     * @param totalQueries Quantidade total de consultas feitas
     * @return Media de perda de packets
     */
    public double lossRate(int totalQueries) {
        return (lostPackets * 100.0)/totalQueries;
    }

    public String getDomain() {return domain;}

    @Override
    public int compareTo(DNSQueryStats o) {
        return Double.compare(this.calcAvg(), o.calcAvg());
    }

    @Override
    public String toString() {
        return "\nStats DNS de " + domain + " (em Ms)" +
               "\n----------------------------------------------" +
               "\nQtd de queries:        " + (times.size() + lostPackets) +
               "\nRecebidos:             " + times.size() +
               "\nPerdidos:              " + lostPackets +
               "\n" +
               "\nRTT min:               " + min() +
               "\nRTT max:               " + max() +
               "\nRTT avg:               " + calcAvg() +
               "\nPerda avg:             " + lossRate(lostPackets + times.size());   
    }
}
