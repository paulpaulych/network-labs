import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class FileExchangeClient {
    private static final int SUCCESS = 0;
    private static final int FAILURE = -1;
    private static final int BUFFER_SIZE = 1024*1024;
    private final byte[] longBuffer = new byte[8];
    private final byte[] buffer = new byte[BUFFER_SIZE];

    public void send(InetSocketAddress target, File file){
        try(Socket socket = new Socket()) {
            if(file.isDirectory()){
                System.out.println(file.getName() + " cannot be sent. It's a directory");
                return;
            }

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
                System.out.printf("Current speed: %.0f KB/sec\n",
                        (double)bytesRead/(double)(System.currentTimeMillis() - lastTs)/1024*1000);
                System.out.printf("Average speed: %.00f KB/sec\n",
                        (double)totalBytesRead/(double)(System.currentTimeMillis() - startTs)/1024*1000);
            }

            socket.getInputStream().read(longBuffer);
            long result = bytesToLong(longBuffer);

            if(result == SUCCESS) {
                System.out.println("File uploaded successfully");
            }else if(result == FAILURE){
                System.out.println("File is not sent. Error occurred.");
            }

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

    private static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

}
