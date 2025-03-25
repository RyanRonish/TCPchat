# TCPchat

1. How is your server able to connect to multiple clients?

The server uses sockets to listen for incoming connections on a specific port, the default is 5000 but the user can input a different one. Each time a new client connects a new client handler thread is created for that client and they are added to the schared list of all active clients. This allows for many users to chat with one another at the same time without any interuptions between chats. 

2. How does your server handle receiving messages from multiple clients at the same time?

Each of the clients has its own client handler thread that listens for incoming messages from each individual user. The treads are constantly listening for new messages coming through from their assigned clients using the buffered reader. Since each user has a thread assigned to them, multiple clients can send messages at the same time without any issues or disrupting each others messages.

3. How does your server forward messages onto multiple clients?

When a message is received from a client the server iterates through the list of connected clients stored in 'users' a CopyOnWriteArrayList and sends the message to all the other clients, ensuring everyone receives the message. I used the CopyOnWrite array list because I found out that that is a list that is thread safe, creating a new copy of the array whenever there is a modification. Also I added a way that the sender doesn't recieve the message they sent though. That would be chaotic and the server checks to make sure that doesn't happen. 

4. How does your server handle a disconnection (client is no longer connected)?

If a client disconnects by closing the connection with 'control C' or an error, the client handler thread recongnizes this when it happens by reading from the input stream. The server then removes the client from the list of active clients and notifies the everyone else in the chat that the user has left.

5. How does your client handle receiving a message from the server while waiting for the user to type their message?

The client runs a seperate threads that are listening for messages to come from the server using the bufferedreader and prints them to the console, while the main thread listens for user input. This ensures the client can both send messages and receive messages at the same time.