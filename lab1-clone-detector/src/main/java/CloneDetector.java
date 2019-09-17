import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class CloneDetector {

    private static final int port = 5555;
    private static final String address = "255.255.255.255";

    public void run() throws IOException {

        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = "hello".getBytes();
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress);
        socket.send(packet);
        socket.close();
    }

    public void listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastAddresses = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()){
            NetworkInterface networkInterface = interfaces.nextElement();

            if(networkInterface.isLoopback() || !networkInterface.isUp()){
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastAddresses::add);

            broadcastAddresses.forEach(x -> System.out.println(x.toString()));
        }


    }
}
