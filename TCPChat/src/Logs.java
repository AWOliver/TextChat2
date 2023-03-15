import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logs{

public BufferedWriter log;

String message;

    public void logInput(String message) {
        try {
            message = this.message;
            log = new BufferedWriter(new FileWriter("logs.txt"));
            log.write(message + "\n\n");
            log.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
