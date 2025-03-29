package org.marketplace;

import org.marketplace.clientmanipulation.ClientThread;
import org.marketplace.persistence.connection.DatabaseConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int PORT = 0000;
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT); Socket socket = new Socket(); DatabaseConnection databaseConnection = new DatabaseConnection("marketplacePersistenceUnit")){

            while(true) {
                Socket received = serverSocket.accept();
                System.out.println("Starting " + received.getInetAddress().getHostAddress());
                //Open a thread for each received client
                new ClientThread(received, databaseConnection).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
