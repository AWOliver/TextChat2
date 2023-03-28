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
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }
    //declares list for users and boolean for shutdown

    @Override
    public void run() {

        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    //creates server and connects clients

    public void broadcast(String message, String nickname) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                if (ch.nickname.equals(nickname)) {
                    continue;
                }
                ch.sendMessage(message);
            }
        }
    }
    //template for message broadcast
    public void shutdown() {
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {

        }
    }
    //shutdown method
    class ConnectionHandler implements Runnable {

        private final Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) { //Creates a method to access a specific client to the server
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname: ");
                nickname = in.readLine(); // sets nickname
                System.out.println(nickname + " connected!");
                broadcast(nickname + " joined the chat!", nickname);
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/newName")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " renamed themselves to " + messageSplit[1], nickname);
                            System.out.println(nickname + " changed nickname to " + messageSplit[1]);
                            nickname = messageSplit[1]; //sets new nickname
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
                        if(nickname.equalsIgnoreCase("Ollibolli")) {
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
