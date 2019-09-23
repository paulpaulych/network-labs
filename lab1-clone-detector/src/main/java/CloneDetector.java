
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloneDetector {

    private static final int bufSize = 64;
    private static final int timeout = 1000;
    private static final int port = 4424;
    private static final String message = "";
    private static final long addressTtl = 3000;


    private final String groupAddress;
    private final DatagramPacket packetToRecv;
    private Map<String, Long> addressAge = new HashMap<>();

    public CloneDetector(String groupAddress) {
        this.groupAddress = groupAddress;
        packetToRecv = new DatagramPacket(new byte[bufSize], bufSize);
    }

    public void run() throws IOException {

        MulticastSocket socket = new MulticastSocket(port);

        System.out.println(socket.getLocalAddress());

        InetAddress group = InetAddress.getByName(groupAddress);
        socket.joinGroup(group);

        byte[] buffer = message.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, group, port);
        while(true){
            socket.send(packetToSend);
            long lastTime = System.currentTimeMillis();
            while(System.currentTimeMillis() - lastTime < timeout){

                socket.setSoTimeout((Long.valueOf(System.currentTimeMillis() - lastTime)).intValue()+1);
                try{
                    socket.receive(packetToRecv);
                }catch (SocketTimeoutException e){
                    continue;
                }

                if(!message.equals(new String(packetToRecv.getData(), 0, packetToRecv.getLength()))){
                    log.warn("not-from-clone message");
                    continue;
                }
                String receivedAddress = packetToRecv.getAddress().getHostAddress();
                Long existed = addressAge.put(receivedAddress, System.currentTimeMillis());
                if(existed == null){
                    System.out.println("====CLONE APPEARED====");
                    printAddresses();
                }
            }

            boolean wereRemoved = addressAge.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() > addressTtl);
            if(wereRemoved){
                System.out.println("====CLONE DISAPPEARED====");
                printAddresses();
            }
        }
    }

    private void printAddresses() {
        Integer i = 1;
        for (String address: addressAge.keySet()) {
            System.out.println(i.toString() + ". " + address);
            i++;
        }
    }
}
