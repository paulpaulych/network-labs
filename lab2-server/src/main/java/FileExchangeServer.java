import lombok.extern.slf4j.Slf4j;


import micrometer.Micrometer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import micrometer.MircometerController;

@Slf4j
public class FileExchangeServer {

    private static final int MAX_CONNECTIONS_NUMBER = 5;

    private MircometerController mircometer = new MircometerController();

    public void run(int port){
        ExecutorService handlers = Executors.newFixedThreadPool(MAX_CONNECTIONS_NUMBER);
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while(true){
                log.debug("waiting for connection..");
                Socket socket = serverSocket.accept();
                log.info("got connection from {}", socket.getRemoteSocketAddress());
                handlers.submit(new ConnectionHandler(socket, mircometer));
            }
        }catch (IOException e){
            log.debug(e.getMessage());
        }
    }

}
