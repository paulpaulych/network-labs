
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloneDetector {

    private static final int bufSize = 64;
    private static final int timeout = 2000;
    private static final int port = 4424;
    private static final String groupAddress = "230.0.0.0";
    private static final String message = "i'm here!";
    private final DatagramPacket packetToRecv;
    private static final long addressTtl = 10000;
    private HashMap<String, Long> addressAge = new HashMap<>();

    public CloneDetector() {
        packetToRecv = new DatagramPacket(new byte[bufSize], bufSize);
    }

    public void run() throws IOException {

        MulticastSocket socket = new MulticastSocket(new InetSocketAddress("0::0", port));

        InetAddress group = InetAddress.getByName(groupAddress);
        socket.joinGroup(group);

        byte[] buffer = message.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, group, port);
        socket.setSoTimeout(timeout);
        while(true){
            socket.send(packetToSend);
            long lastTime = System.currentTimeMillis();
            while(System.currentTimeMillis() - lastTime < timeout){

                socket.setSoTimeout(timeout);
                try{
                    socket.receive(packetToRecv);
                }catch (SocketTimeoutException e){
                    continue;
                }

                if(!message.equals(new String(packetToRecv.getData(), 0, packetToRecv.getLength()))){
                    log.info("not-from-clone message");
                    continue;
                }

                String receivedAddress = packetToRecv.getAddress().getHostAddress();

                addressAge.put(receivedAddress, System.currentTimeMillis());
            }

            addressAge.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() > addressTtl);

            printAddresses();
        }
    }

    private void printAddresses() {
        Integer i = 1;
        System.out.println("====CLONES====");
        for (String address: addressAge.keySet()) {
            System.out.println(i.toString() + ". " + address);
        }
    }
}
