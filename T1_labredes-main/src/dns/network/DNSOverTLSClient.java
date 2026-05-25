package dns.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class DNSOverTLSClient implements DNSNetworkClient{
    private InetAddress dnsServer;
    private int port;

    public DNSOverTLSClient(InetAddress dnsServer, int port) {
        this.dnsServer = dnsServer;
        this.port = port; 
    }

    @Override
    public byte[] sendAndReceive(byte[] requestBytes) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        
        try(SSLSocket socket = (SSLSocket) factory.createSocket(dnsServer, port)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            socket.setSoTimeout(2000);

            //tam da mensagem
            out.writeShort(requestBytes.length);

            //escrevendo mensagem
            out.write(requestBytes);
            out.flush();

            //resposta
            int responseLength = in.readUnsignedShort();
            byte[] responseBytes = new byte[responseLength];

            in.readFully(responseBytes);

            return responseBytes;
        }
    }

    @Override
    public void changeServer(InetAddress dnsServer) {
        this.dnsServer = dnsServer;
    }

    @Override
    public String toString() {
        return this.dnsServer.toString();
    }
}