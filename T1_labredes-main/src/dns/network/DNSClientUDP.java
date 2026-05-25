package dns.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Classe responsavel pelo envio dos dados aos servidores DNS
 */
public class DNSClientUDP implements DNSNetworkClient{
    private InetAddress dnsServer;
    private int port;
    
    /**
     * Cria o cliente DNS com dnsServer
     * @param port 
     *        Usar 53 para UDP
     * 
     * @throws IOException 
     *         Se nao for possivel conectar ao servidor
     */
    public DNSClientUDP(InetAddress dnsServer, int port) throws IOException {
        this.dnsServer = dnsServer;
        this.port = port; 
    }

    /**
     * Envia a mensagem codificada para o servidor definido nele e espera resposta.
     * @param request 
     *        {@code byte[]} que tem a mensagem codificada.
     * 
     * @return {@code byte[]} da resposta.
     *  
     * @throws IOException
     *         Caso algum erro ocorrer no socket.
     */
    @Override
    public byte[] sendAndReceive(byte[] request) throws IOException, SocketTimeoutException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(request, request.length, dnsServer, port);
        
        socket.setSoTimeout(2000);
        socket.send(packet);

        byte[] responseBuffer = new byte[512];

        DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(response);

        socket.close();
        return Arrays.copyOf(response.getData(), response.getLength());
    }

    /**
     * Troca servidor DNS usado na consulta.
     * @param dnsServer
     *        Servidor novo a ser usado.
     */
    @Override
    public void changeServer(InetAddress dnsServer) {
        this.dnsServer = dnsServer;
    }

    @Override
    public String toString() {
        return this.dnsServer.toString();
    }
}
