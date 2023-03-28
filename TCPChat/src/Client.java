import java.io.*;
import java.net.Socket;                 //import


public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;       //Boolean to declare shutdown

    Logs logs = new Logs();
    @Override
    public void run() {
        try {

            //Activates client side and creates passage for messages.
            System.out.println("Welcome to Thunder Chat!\n");
            System.out.println("Here you can communicate with your friends in real time, while simultaneously being granted some useful functions\n");
            System.out.println("");
            System.out.println("");
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
        } catch (IOException e) {
            // ignore
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {  //creates a variable for input by client
                    String message = inReader.readLine();
                    if(message != null) {
                        logs.logInput(": " + message);
                    }
                    if (message.equals("/quit")) {  //quits program
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message); //sends message
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client(); //runs methods
        client.run();
    }
}
