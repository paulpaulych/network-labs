import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.InetAddress;

@RequiredArgsConstructor
public class FileExchangeClient {

    private final InetAddress targetAddress;
    private final int port;
    private final File file;

    public void run(){

    }
}
