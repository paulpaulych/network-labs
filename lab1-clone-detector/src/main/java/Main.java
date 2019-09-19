import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        new CloneDetector(args[0]).run();
    }
}
