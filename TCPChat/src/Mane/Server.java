package Mane;

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

    public Server() { //declares list for users and boolean for shutdown
        connections = new ArrayList<>();
        done = false;
    }
    @Override
    public void run() { //creates server and connects clients

        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                pool.execute(handler);
                connections.add(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    public void broadcast(String message, String nickname) { //template for message broadcast
        for (ConnectionHandler ch : connections) {

            if (ch != null) {
                if (ch.getNickname() == null) {
                    System.out.println(ch);
                    continue;
                }

                System.out.println(ch.getNickname());
                if (ch.getNickname().equals(nickname)) {
                    continue;
                }
                ch.sendMessage(message);
            }
        }
    }
    public void broadcastSingle(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                if (ch.getNickname() == null) {
                    System.out.println(ch);
                    continue;
                }

                System.out.println(ch.getNickname());
                if (ch.getNickname().equals("ollibolli")) {
                    ch.sendMessage(message);
                    break;
                }
            }
        }
    }
    public void shutdown() { //shutdown method
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException ignored) {

        }
    }
    class ConnectionHandler implements Runnable {

        private final Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public String nickname;

        public ConnectionHandler(Socket client) { //Creates a method to access a specific client to the server
            this.client = client;
        } //connects client to method

        @Override
        public void run() { //Creates i/o  and commands for clients
            try {
                Rome rome = new Rome();
                rome.createFile("rome.txt");
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
                        } else {
                            if (nickname.equalsIgnoreCase("Ollibolli")) {
                                broadcast("(Admin) " + nickname + ": " + message, nickname);
                            }
                            else {
                                if(message.startsWith("/rome")){
                                    broadcast(nickname + ": Here is some information: \t" + Rome.readFromFile("rome.txt"), nickname);
                                }
                                else if(message.startsWith("/secret") && nickname.equals("obo")){
                                    String[] messageSplit = message.split(" ", 2);
                                    if (messageSplit.length == 2 && messageSplit[1].equals("ollibolli")) {
                                        broadcastSingle(SecretClient.secretMessage(messageSplit[1]));
                                        out.println("Secret message sent" );
                                    } else {
                                        out.println("No the right reciever provided!");
                                    }
                                }
                                else {
                                    broadcast(nickname + ": " + message, nickname);
                                }
                            }
                        }
                    }

                }catch(IOException e){
                    shutdown();
                }
        }
         public void sendMessage(String message) {
            out.println(message);
        } //sends message
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
        public String getNickname() {
            return nickname;
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
         server.run();
    }
}
