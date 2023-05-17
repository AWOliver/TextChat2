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

//This contains the servers functionality
public class Server implements Runnable {

    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    //declares list for users and boolean for shutdown
    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    //creates server and connects clients
    @Override
    public void run() {

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

    //template for message broadcast
    public void broadcast(String message, String nickname) {
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

    //a method for sending messages to only admin
    public void broadcastSecret(String message) {
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

    //shutdown method
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
        } catch (IOException ignored) {

        }
    }

    //This class is used to handle the communications between clients
    class ConnectionHandler implements Runnable {

        private final Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public String nickname;

        //connects client to the socket through method
        public ConnectionHandler(Socket client) { //Creates a method to access a specific client to the server
            this.client = client;
        }

        //Creates i/o  and commands for clients
        @Override
        public void run() {
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
                                        broadcastSecret(SecretClient.secretMessage(messageSplit[1]));
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

        //A method for writing a message
         public void sendMessage(String message) {
            out.println(message);
        }

        //Used to end communication for clients
        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                System.out.println("could not shutdown");
            }
        }

        //Used to connect a socket port to a nickname
        public String getNickname() {
            return nickname;
        }
    }

    //runs the program
    public static void main(String[] args) {
        Server server = new Server();
         server.run();
    }
}
