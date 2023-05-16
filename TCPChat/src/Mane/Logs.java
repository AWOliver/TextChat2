package Mane;

import java.io.*;

public class Logs
{

public static BufferedWriter log;


    public static void logInput(String message) {
        try {
            log = new BufferedWriter(new FileWriter("logs.txt"));
            log.write(message + "\n");
            log.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showLogs(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("logs.txt"));

            String logText;
            while((logText = reader.readLine()) != null){
                System.out.println(logText);
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
