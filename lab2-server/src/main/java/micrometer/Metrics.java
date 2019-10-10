package micrometer;

import lombok.Getter;

public class Metrics {

    private final long startTs = System.currentTimeMillis();
    private long lastTs = startTs;

    @Getter
    private int totalBytesRead = 0;
    @Getter
    private double currentSpeed = 0;
    @Getter
    private double averageSpeed = 0;

    private int lastBytesRead = 0;

    public void addBytesRead(int bytesRead){
        totalBytesRead += bytesRead;
        lastBytesRead += bytesRead;
        if(System.currentTimeMillis() - lastTs < 500){
            return;
        }
        long nowTs = System.currentTimeMillis();
        currentSpeed = bytesToKB(lastBytesRead) / millisToSec(nowTs - lastTs);
        averageSpeed = bytesToKB() / millisToSec(nowTs - startTs);
        System.out.println("currentSpeed " + currentSpeed);
        System.out.println("averageSpeed " + averageSpeed);
        System.out.println("lastBytesRead " + lastBytesRead);
        System.out.println("lastTs " + lastTs);
        System.out.println("nowTs " + nowTs);
        lastTs = System.currentTimeMillis();
        lastBytesRead = 0;
    }

    private double millisToSec(long millis){
        return millis* (double)1000;
    }

    private double bytesToKB(long bytes){
        return bytes/(double)1024;
    }



    public long getTotalTime(){
        return System.currentTimeMillis() - startTs;
    }
}
