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

    private ArrayList<ConnectionHandler> connections;
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

    public void broadcast(String message, String nickname) { //Send message to all people in the ConnectionHandler
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                if (ch.nickname.equals(nickname)) { //Do not send message to message owner twice.(NEEDS TO BE REMOVED WHEN GRAPHICS IS DONE)
                    continue;
                }
                ch.sendMessage(message); //Send the message if it isn??t the message owner.
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

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) { //Creates a method to access a specific client to the server
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true); //Initializing writer
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initializing reader
                out.println("Please enter a nickname: ");
                nickname = in.readLine(); // sets nickname
                System.out.println(nickname + " connected!"); //Loads the broadcast function
                broadcast(nickname + " joined the chat!", nickname);
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/nickname")) { //NICK COMMAND
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " renamed themselves to " + messageSplit[1], nickname);
                            System.out.println(nickname + " changed nickname to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickname);
                        } else {
                            out.println("No nickname provided!");
                        }
                    } else if (message.startsWith("/quit")) {
                        shutdown();
                        broadcast(nickname + " left the chat!", nickname);
                        System.out.println(nickname + " disconnected!");
                        //shutdown();
                    } else {
                        if(nickname.equalsIgnoreCase("panda19")) {
                            broadcast("(Owner) " + nickname + ": " + message, nickname);
                            System.out.println("Owner Found");
                        } else {
                            broadcast(nickname + ": " + message, nickname);
                        }
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }
        public void shutdown() {
            try {
                in.close();
                out.close();
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
