import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class FileExchangeClient {


    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];


    public void send(InetSocketAddress target, File file){

        try(Socket socket = new Socket()) {

            socket.connect(target);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file);


            while(fis.read(buffer, 0, BUFFER_SIZE) != -1){
                os.write(buffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
