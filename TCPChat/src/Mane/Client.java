package Mane;
import java.io.*;
import java.net.Socket;                 //import
import java.util.Scanner;


public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    @Override
    public void run() { //Activates client side, creates interface and creates passage for messages.
        try {
            info();
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

    public void info(){
        System.out.println("Welcome to Thunder Chat!\n");
        System.out.println("Here you can communicate with your friends in real time, \nwhile simultaneously being granted some useful functions\n");
        System.out.println("These are the instructions for the Thunder Chat\n");
        System.out.println("Commands:\n");
        System.out.println("/quit: write to exit the chat\n");
        System.out.println("/secret: as obo you can use this command for secret messages");
        System.out.println("/rome: get some information about the roman empire");
        System.out.println("/newName: Write this command and the write your new desired nickname\n");
        System.out.println("ollibolli: If you enter this nickname you will be granted Admin\n");
        System.out.println("obo: This username will give you access to secret messaging to admin");
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
