package Mane;

import java.io.*;
import java.net.Socket;                 //import
import java.util.Scanner;


public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    Logs logs = new Logs();
    @Override
    public void run() { //Activates client side, creates interface and creates passage for messages.
        try {

            System.out.println("Welcome to Thunder Chat!\n");
            System.out.println("Here you can communicate with your friends in real time, \nwhile simultaneously being granted some useful functions\n");
            Scanner switchScan = new Scanner(System.in);
            System.out.println("Start chatting: Press 1");
            System.out.println("Instructions: Press 2");
            System.out.println("Logs: Press 3");
            client = new Socket("localhost", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {        //shuts down client
                client.close();
            }
        } catch (IOException ignored) {
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() { //
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if(message != null) {
                        logs.logInput(message);
                    }
                    assert message != null;
                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client(); //runs program
        client.run();
    }
}
