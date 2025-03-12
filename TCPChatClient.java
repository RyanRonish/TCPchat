import java.io.*;
import java.net.*;

public class TCPChatClient {
    private static final String default_host = "localhost"; // 127.0.0.1
    private static final int default_port = 5000;    // random port # use java TCPChatClient 127.0.0.1 6000 if 5000 doesn't work

    public static void main(String[] args) {
        String host = default_host;
        int port = default_port;

        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port " + default_port);
            }
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to chat server at " + host + ":" + port);

            // Thread to listen for incoming messages
            Thread incoming_messages = new Thread(() -> {
                String message_to_server;
                try {
                    while ((message_to_server = in.readLine()) != null) {
                        System.out.println("\n" + message_to_server);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            incoming_messages.start();

            // Main loop to send user messages
            String users_message;
            while ((users_message = userInput.readLine()) != null) {
                out.println(users_message);
                System.out.print("> ");
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}