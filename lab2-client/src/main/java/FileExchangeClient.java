import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class FileExchangeClient {
    private static final int SUCCESS = 0;
    private static final int FAILURE = -1;
    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    public void send(InetSocketAddress target, File file){

        try(Socket socket = new Socket()) {

            socket.connect(target);

            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file);

            byte[] length = longToBytes(file.length());
            os.write(length);

            byte[] fileNameBytes = file.getName().getBytes();
            length = longToBytes(fileNameBytes.length);
            os.write(length);
            os.write(fileNameBytes, 0, fileNameBytes.length);

            long fileSize = file.length();

            long startTs = System.currentTimeMillis();

            long totalBytesRead = 0;
            while(totalBytesRead < fileSize){
                long lastTs = System.currentTimeMillis();
                int bytesRead = fis.read(buffer);
                totalBytesRead += bytesRead;
                os.write(buffer, 0, bytesRead);
                System.out.println("Current speed: "
                        + (double)(System.currentTimeMillis() - lastTs)/1024/bytesRead + "KB/sec");
                System.out.println("Average speed: "
                        + (double)(System.currentTimeMillis() - startTs)/1024/totalBytesRead + "KB/sec");
            }
            int result = 0;
//            socket.getInputStream().read(result);
//            if(result == SUCCESS){
//                System.out.println("File uploaded successfully");
//            }else{
//                System.out.println("File is not sent. Error occurred.");
//
//            }

            } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

}
