package Mane;
import java.io.*;
import java.net.Socket;

//contains the client side of the chat
public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    //Activates client side, creates interface and creates passage for messages.
    @Override
    public void run() {
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

    //prints information for client in the start
    public void info(){
        System.out.println("\nWelcome to Thunder Chat!\n");
        System.out.println("Here you can communicate with your friends in real time, \nwhile simultaneously being granted some useful functions\n");
        System.out.println("These are the instructions for the Thunder Chat\n\n");
        System.out.println("Commands:\n");
        System.out.println("/quit: write to exit the chat\n");
        System.out.println("/secret: as obo you can use this command for secret messages\n");
        System.out.println("/rome: get some information about the roman empire\n");
        System.out.println("/newName: Write this command and the write your new desired nickname\n");
        System.out.println("ollibolli: If you enter this nickname you will be granted Admin\n");
        System.out.println("obo: This username will give you access to secret messaging to admin\n");
    }

    //ends clients connection to server
    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException ignored) {
        }
    }

    //Creates different scenarios for client commands and its result
    class InputHandler implements Runnable {

        //-||-
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if(message != null){
                        if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                        } else {
                        out.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    //runs client
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
