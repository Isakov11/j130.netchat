package j130.net;

import j130.net.ui.*;
import j130.net.client.ClientSocket;
import j130.net.server.ServerSocketListener;

public class Application {
    
    public static void main(String[] args) throws Exception {
        runServer(29321);
        runClient(29321);
        //runClient(29321);
        //runClient(29321);
    }
    
    private static void runServer(int port) {
        
        ServerSocketListener serverSocket = new ServerSocketListener(port);
        Thread serverThread = new Thread(serverSocket,"serverSocket");
        serverThread.start();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerFrame().setVisible(true);
            }
        });
        
    }

    private static void runClient(int port) {
        ClientSocket clientSocket = new ClientSocket(port);
        Thread clientThread = new Thread(clientSocket,"clientSocket");
        clientThread.start();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientFrame(clientSocket).setVisible(true);
            }
        });
    }
}
