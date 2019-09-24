import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ConnectionHandler extends Thread {

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Object fileLock = new Object();

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        try (InputStream is = socket.getInputStream()) {
            while(is.read(buffer, 0, buffer.length) != -1){
                OutputStream fos = new FileOutputStream(getFile("file.txt"));
            }
        }catch (IOException e){
            log.error(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private File getFile(String fileName) throws IOException {
        File file = new File("uploads/" + fileName);

        int i = 0;
        synchronized (fileLock){
            while(file.exists()){
                i++;
                file = new File("uploads/" + fileName + "(" + i +")");
            }
            file.createNewFile();
        }
        return file;
    }

}
