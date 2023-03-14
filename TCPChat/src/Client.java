import java.io.*;
import java.net.Socket;                 //import


public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public BufferedWriter log;
    private boolean done;       //instance variable

    @Override
    public void run() {
        try {

            client = new Socket("localhost", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));        //Creates input and output between the client and server
            log = new BufferedWriter(new FileWriter("logs.txt"));

            InputHandler inHandler = new InputHandler();        //Connects to a thread where the user can communicate with the server
            Thread t = new Thread(inHandler);
            t.start();  //Activates thread

            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage); //
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
                    log.write(message + "\n\n");
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
