import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;                 //import


public class Client implements Runnable, ActionListener {

    JFrame frame;
    JPanel panel;
    JButton button;
    //JLabel label;

    //JTextField tField;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;       //instance variable

    @Override
    public void run() {
        try {

            frame = new JFrame();
            panel = new JPanel();

            frame.setSize(400, 300);
            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);        //Creates GUI

            panel.setSize(400, 300);

            button = new JButton();





            client = new Socket("localhost", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));        //Creates input and output between the client and server

            InputHandler inHandler = new InputHandler();        //Connects to a thread where the user can communicate with the server
            Thread t = new Thread(inHandler);
            t.start();  //Activates thread

            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage); //sends message with, out function
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

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {  //creates a variable for input by client
                    String message = inReader.readLine();
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
