package Mane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logs extends Client
{

public static BufferedWriter log;


    public static void logInput(String message) {
        try {
            log = new BufferedWriter(new FileWriter("logs.txt"));
            log.write(message + "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
