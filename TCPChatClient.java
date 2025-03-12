import java.io.*;
import java.net.*;

public class TCPChatClient {
    //default server addy
    private static final String default_host = "localhost"; // 127.0.0.1
    private static final int default_port = 5000;    // random port # use java TCPChatClient 127.0.0.1 6000 if 5000 doesn't work

    public static void main(String[] args) {
        String host = default_host;
        int port = default_port;

        // checks custom host and port if provided
        if (args.length > 0) {
            host = args[0]; // server addy
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]); ////custom port
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port " + default_port);
            }
        }

        try (Socket socks = new Socket(host, port);  // connects to the server
             PrintWriter output = new PrintWriter(socks.getOutputStream(), true); // output stream
             BufferedReader input = new BufferedReader(new InputStreamReader(socks.getInputStream())); // input stream
             BufferedReader user_text = new BufferedReader(new InputStreamReader(System.in))) {  // usre input is read

            System.out.println("Connected to chat server at " + host + ":" + port);

            // listen for incoming messages thread 
            Thread incoming_messages = new Thread(() -> {
                String message_to_server;
                try {
                    while ((message_to_server = input.readLine()) != null) { // reads messages from server
                        System.out.println("\n" + message_to_server);
                        System.out.print("> "); // prompts user for input
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            incoming_messages.start(); // thread start

            // Main loop to send user messages
            String users_message;
            while ((users_message = user_text.readLine()) != null) {
                output.println(users_message);
                System.out.print("> "); // prompt format
            }

        } catch (IOException e) {
            System.err.println("Error connecting: " + e.getMessage());
        }
    }
}