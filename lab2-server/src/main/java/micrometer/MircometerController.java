package micrometer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MircometerController implements Micrometer {

    private static final int METRICS_PRINTER_FIXED_DELAY = 1000;

    private Map<String, Metrics> metricsMap = new ConcurrentHashMap<>();

    private ExecutorService timer = Executors.newSingleThreadExecutor();

    public MircometerController(){
        timer.submit(()->{
            while(!Thread.currentThread().isInterrupted()){
                try {
                    Thread.sleep(METRICS_PRINTER_FIXED_DELAY);
                } catch (InterruptedException e) {
                    System.out.println("timer finished up");
                    return;
                }
                printSpeedInfo();
            }
        });
    }

    public void stop(){
        timer.shutdownNow();
    }

    @Override
    public void attach(String fileName){
        metricsMap.put(fileName, new Metrics());
    }

    @Override
    public void publishBytesRead(String fileName, int bytesRead){
        metricsMap.get(fileName).addBytesRead(bytesRead);
    }

    @Override
    public void detach(String fileName){
        printSpeedInfo();
        metricsMap.remove(fileName);
    }

    @Override
    public int getTotalBytesRead(String fileName){
        return metricsMap.get(fileName).getTotalBytesRead();
    }

    @Override
    public long getTotalTime(String fileName){
        return metricsMap.get(fileName).getTotalTime();
    }

    private void printSpeedInfo(){
        if(metricsMap.isEmpty()){
            return;
        }
        System.out.println("==============================================================================================================");
        System.out.println("Speed info:");
        metricsMap.forEach((k, v)->{
            String s = String.format("File: %s. Current Speed: %.5f KB/s, Average speed: %.5f KB/s", k, v.getCurrentSpeed(), v.getAverageSpeed());
            System.out.println(s);
        });
    }

}
