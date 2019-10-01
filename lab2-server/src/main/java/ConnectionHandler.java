
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
public class ConnectionHandler extends Thread {

    private static final int FAILURE = -1;
    private static final int SUCCESS = 0;

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        FileOutputStream fos = null;
        try (InputStream is = socket.getInputStream()) {
            byte[] fileLengthBytes = new byte[8];
            is.read(fileLengthBytes);
            long fileLength = bytesToLong(fileLengthBytes);
            log.debug("file size is {}({})", fileLengthBytes, fileLength);

            byte[] fileNameLengthBytes = new byte[8];
            is.read(fileNameLengthBytes);
            long fileNameLength = bytesToLong(fileNameLengthBytes);
            log.debug("file name size is {}({})", fileNameLengthBytes, fileNameLength);

            byte[] fileNameBytes = new byte[(int)fileNameLength];
            is.read(fileNameBytes);

            String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
            log.debug("file name is {}", fileName);

            File file = getFile(fileName);
            fos = new FileOutputStream(file);

            long totalBytesReceived = 0;
            while(totalBytesReceived < fileLength){
                int bytesReceived = is.read(buffer);
                totalBytesReceived += bytesReceived;
                fos.write(buffer, 0, bytesReceived);
            }
            fos.close();

            if(file.length() == fileLength) {
                socket.getOutputStream().write(SUCCESS);
            }else {
                socket.getOutputStream().write(FAILURE);
            }

            log.info("saved file \"{}\"", file.getName());
        }catch (IOException e){
            log.error(e.getMessage());
        }finally {
            try {
                if(fos != null ) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getFile(String fileName) throws IOException{
        Path dir = Paths.get("uploads");
        try{
            Files.createDirectory(dir);
        }catch (FileAlreadyExistsException e){
            log.trace("uploads dir already exists");
        }

        Path file = Paths.get("uploads/" + fileName);

        int i = 0;
        while(true){
            try {
                Files.createFile(file);
                break;
            } catch (FileAlreadyExistsException e) {
                i++;
                file = Paths.get("uploads/" + "(" + i +")" + fileName);
            }
        }
        return file.toFile();
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
