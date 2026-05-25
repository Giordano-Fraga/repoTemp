package dns.network;

import java.io.IOException;
import java.net.InetAddress;

public interface DNSNetworkClient {
    byte[] sendAndReceive(byte[] requestBytes) throws IOException;
    void changeServer(InetAddress dnsServer);
}
