package apg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NonBlockingTCPListener extends Thread {

    private ServerSocket serverSocket;

    public NonBlockingTCPListener(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                // Handle the connection in a new thread
                new Thread(() -> APGManager.handleConnection(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
}

