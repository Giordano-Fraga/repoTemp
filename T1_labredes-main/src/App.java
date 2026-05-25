import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import dns.codec.*;
import dns.network.*;
import dns.model.DNSMessage;
import dns.model.DNSQuestion;
import dns.model.enums.DNSClass;
import dns.model.enums.DNSType;

public class App {
    private static boolean debug = false;
    private static int id = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        DNSNetworkClient dnsClient = null;
        boolean exit = false;
        List<DNSQueryStats> timeHistory = new LinkedList<>();
        InetAddress currDNS = null;

        //================================================================================
        // 1) Criando socket (UDP ou TCP over TLS)
        //================================================================================
        System.out.println("Escolha o numero do tipo de comunicacao a ser realizada:");
        System.out.println("1) UDP\n2) TCP over TLS");
        int clientType = input.nextInt();

        switch(clientType) {
            case 1:
                try {
                    currDNS = InetAddress.getByName("8.8.8.8");
                    dnsClient = new DNSClientUDP(currDNS, 53);
                } catch (IOException e) {
                    System.err.println("Erro ao criar o cliente DNS UDP");
                    e.printStackTrace();
                    input.close();
                    return;
                }
                break;

            case 2:
                try {
                    currDNS = InetAddress.getByName("8.8.8.8");
                    dnsClient = new DNSOverTLSClient(currDNS, 853);
                } catch (IOException e) {
                    System.err.println("Erro ao criar o cliente DNS over TLS");
                    e.printStackTrace();
                    input.close();
                    return;
                }
                break;
        }
        System.out.println("Socket criado com sucesso!");
        input.nextLine();

        //================================================================================
        // 2) Recebendo dominio para consulta/query 
        //================================================================================
        
        while(true) {
            DNSQueryStats stats = null;
            String domain = "";
            int qtd = 1;

            System.out.println("Digite o IP do servidor DNS e dominio a receber a consulta, junto a quantidade a enviar (ex: 8.8.8.8 example.com 10)");
            System.out.println("[Digite \"debug\" para ativa-lo ou \"exit\" para sair]");
            String[] check = input.nextLine().split(" ");

            for(String s : check) {
                if(s.equals("debug")) {
                    debug = true;
                }
                else if(itsInt(s)) {qtd = Integer.parseInt(s);}
                else if(s.equals("exit")) {exit = true;}
                else if(itsIP(s)) {
                    if(debug) {System.out.println("IP DETECTADO = " + s);}
                    if(debug) {System.out.println("currDNS atual = " + currDNS.toString());}

                    try {
                        currDNS = InetAddress.getByName(s);
                    } catch (UnknownHostException e) {
                        System.err.println("Erro na transformacao de string para IP");
                        e.printStackTrace();
                    };

                    for(DNSQueryStats qs : timeHistory) {
                        if(qs.getDomain().equals(s)) {stats = qs;}
                    }
                    if(stats == null) {stats = new DNSQueryStats(s);}
                }
                else{domain = s;}
            }

            dnsClient.changeServer(currDNS);
            if(debug) {System.out.println("Novo endereco = " + dnsClient);}

            if(exit) {break;}

            id++;
            DNSQuestion question = new DNSQuestion(domain, DNSType.A, DNSClass.IN);
            DNSMessage msg = new DNSMessage(id, question);

            if(debug) {
                System.out.println();
                System.out.println("ENVIANDO " + qtd + " mensagens para " + domain);
                System.out.println(msg);
            }

            //================================================================================
            // 3) Codificando e enviando a mensagem DNS <n vezes>
            //================================================================================
            
            for(int i = 0; i < qtd; i++){
                byte[] msgBytes = DNSEncoder.encode(msg);

                byte[] msgRespBytes = new byte[0];

                long start = System.nanoTime();

                try {
                    msgRespBytes = dnsClient.sendAndReceive(msgBytes);
                } catch (IOException e) {
                    System.err.println("Erro no envio da mensagem:");
                    
                    if (e instanceof SocketTimeoutException) {
                        System.err.println("- Timeout");
                        stats.addLoss();
                    }
                    //e.printStackTrace();
                    continue;
                }

                long end = System.nanoTime();
                long rttMs = (end - start)/1_000_000;

                //================================================================================
                // 4) Decodificando e mostrando a resposta DNS <n vezes>
                //================================================================================

                System.out.println();
                System.out.println("(" + i +") RESPOSTA de " + domain);
                DNSMessage msgResp = DNSDecoder.decode(msgRespBytes);

                System.out.println(msgResp);

                stats.addSuccess(rttMs);
            }

            if(!timeHistory.contains(stats)) {timeHistory.add(stats);}
            System.out.println(stats);
            debug = false;
        }

        Collections.sort(timeHistory);

        System.out.println("\nComparacao de tempo (Ms)" +
                           "\n------------------------------");
                           
        for(DNSQueryStats dnsS : timeHistory) {
            System.out.println(dnsS.getDomain() + ": " + dnsS.calcAvg());
        }

        input.close();
    }

    public static boolean itsInt(String s) {
        if(s == null || s.isEmpty()) {return false;}

        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean itsIP(String s) {
        if(s == null || s.isEmpty()) {return false;}

        //separa pelo "."
        String[] parts = s.split("\\.");

        //checa se tem 4 partes a string
        if(parts.length != 4) {
            return false;
        }

        //checa se cada parte do IP eh valido
        for(String part : parts) {
            try {
                int num = Integer.parseInt(part);

                if(num < 0 || num > 255) {return false;}

            } catch(NumberFormatException e) { //se deu errado, n era numero
                return false;
            }
        }

        return true;
    }
}
