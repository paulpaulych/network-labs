import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FileExchangeServer {

    private static final int MAX_CONNECTIONS_NUMBER = 5;

    public void run(int port){
        ExecutorService handlers = Executors.newFixedThreadPool(MAX_CONNECTIONS_NUMBER);
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while(true){
                log.debug("waiting for connection..");
                Socket socket = serverSocket.accept();
                log.info("got connection from {}", socket.getRemoteSocketAddress());
                handlers.submit(new ConnectionHandler(socket));
            }
        }catch (IOException e){
            log.debug(e.getMessage());
        }
    }

}
