package Mane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logs{

public BufferedWriter log;


    public void logInput(String message) {
        try {
            log = new BufferedWriter(new FileWriter("logs.txt"));
            log.write(message + "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
