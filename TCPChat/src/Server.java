import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server; //A server socket waits for requests to come in over the network. It performs some operation based on that request, and then possibly returns a result to the requester.
    private boolean done;
    private ExecutorService pool;

    public Server() { //Class constructor
        connections = new ArrayList<>(); //Initializing the arraylist
        done = false; //Determining value for shutdown
    }

    @Override
    public void run() { //Method that runs when application starts.

        try {
            server = new ServerSocket(9999); //Initializing server
            pool = Executors.newCachedThreadPool(); //Initializing pool
            while (!done) {
                Socket client = server.accept(); //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                ConnectionHandler handler = new ConnectionHandler(client); //creates a user with a given socket
                connections.add(handler); //Appends the specified element to the end of this list.
                pool.execute(handler); //gives the user a thread to communicate on
            }
        } catch (IOException e) { //Exits program if try does not work
            shutdown();
        }

    }

    public void broadcast(String message, String nickname) {
        for (ConnectionHandler ch : connections) { //Send message to all people in the ConnectionHandler
            if (ch != null) { //Do not send message to message owner twice.(NEEDS TO BE REMOVED WHEN GRAPHICS IS DONE)
                if (ch.nickname.equals(nickname)) { //Send the message if it isn´t the message owner.
                    continue;
                }
                ch.sendMessage(message);
            }
        }
    }
    public void shutdown() { //Close and shutdown
        try {
            done = true; //Exits the sockets accept process
            pool.shutdown(); //Turns off the pool of connections between server and clients
            if (!server.isClosed()) {
                server.close(); //Makes sure the server turns off
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();      //Cancelling each socket with a client.
            }
        } catch (IOException e) {
            // ignore
        }
    }

    class ConnectionHandler implements Runnable { //Inner Class that represent all people that connects via a client.

        private final Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) { //Creates a method to access a specific client to the server
            this.client = client;
        } //tells the method o use a specific client

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true); //Initializing writer
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initializing reader
                out.println("Please enter a nickname: ");
                nickname = in.readLine(); // sets nickname
                System.out.println(nickname + " connected!"); //Writes confirmation in the chat
                broadcast(nickname + " joined the chat!", nickname); //sends messages to the other users
                String message;
                while ((message = in.readLine()) != null) { //proceed if there is a message
                    if (message.startsWith("/nickname")) { //command to rename
                        String[] messageSplit = message.split(" ", 2); //tells where to split the message
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " renamed themselves to " + messageSplit[1], nickname); //broadcasts the renaming of a client
                            System.out.println(nickname + " changed nickname to " + messageSplit[1]); //prints in chat
                            nickname = messageSplit[1]; //sets new nickname
                            out.println("Successfully changed nickname to " + nickname); //makes sure the process finished
                        } else {
                            out.println("No nickname provided!"); //if earlier commando fails
                        }
                    } else if (message.startsWith("/quit")) {
                        shutdown();
                        broadcast(nickname + " left the chat!", nickname);
                        System.out.println(nickname + " disconnected!");
                        //shutdown();                                       //disconnects client if commando is given
                    } else {
                        if(nickname.equalsIgnoreCase("Ollibolli")) {
                            broadcast("(Owner) " + nickname + ": " + message, nickname);
                            System.out.println("Owner Found");                              //Creates special message when owner is writing
                        } else {
                            broadcast(nickname + ": " + message, nickname); //default message
                        }
                    }
                }
            } catch (IOException e) {
                shutdown();         //if nothing works, turn off
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }
        public void shutdown() {
            try {
                in.close();
                out.close();                //Shuts down server and kicks clients
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
         server.run();
    }
}
