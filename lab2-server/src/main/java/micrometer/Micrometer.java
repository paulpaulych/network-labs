package micrometer;

public interface Micrometer {

    void attach(String fileName);

    void publishBytesRead(String fileName, int bytesRead);

    void detach(String fileName);

    int getTotalBytesRead(String fileName);

    long getTotalTime(String fileName);
}
