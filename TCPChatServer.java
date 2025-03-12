import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TCPChatServer {
    private static final int default_port = 5000;
    // using copyonwritearraylist instead of normal arraylist because of threading, arraylist is not safe by default 
    //creates new copy of the list every time it is changed
    private static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int server_port = default_port;

        // checks if the port num is provided by the user
        if (args.length > 0) {
            try {
                server_port = Integer.parseInt(args[0]); // makes the string and int 
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using original port " + default_port);
            }
        }

        //creates the server socket
        try (ServerSocket server_socket = new ServerSocket(server_port)) {
            System.out.println("Chat server started on port " + server_port);

            //accepts the new connections as they get added
            while (true) {
                Socket client_socket = server_socket.accept();
                User client_handler = new User(client_socket); //new handler for client
                users.add(client_handler);
                client_handler.start(); //starts the thread for new client
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static class User extends Thread {
        private Socket sock;
        private PrintWriter output;
        private BufferedReader input;
        private String client_number;

        // stores the ip and port
        public User(Socket socket) {
            this.sock = socket;
            this.client_number = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }

        public void run() {
            try {
                // input and output streams to communicate with each other
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                output = new PrintWriter(sock.getOutputStream(), true);
                System.out.println("New user has connected: " + client_number);
                get_new_message("User " + client_number + " has joined the chat."); // lets others in the chat know when you joined

                String message_from_user;
                while ((message_from_user = input.readLine()) != null) {
                    System.out.println("Received from " + client_number + ": " + message_from_user);
                    get_new_message(client_number + ": " + message_from_user); // message goes to all other clients
                }
            } catch (IOException e) {
                System.out.println("Client has disconnected: " + client_number); 
            } finally {
                disconnect_user(); // disconnects user when they leave control C
            }
        }

        private void get_new_message(String message) {
            for (User client : users) {
                // message isn't sent to the user who wrote the message
                if (client != this) {
                    client.output.println(message);
                }
            }
        }

        // disconnects user when they leave control C
        private void disconnect_user() {
            try {
                users.remove(this);
                sock.close();
                get_new_message("User " + client_number + " has left the chat."); // message to other users someone has left
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
    }
}