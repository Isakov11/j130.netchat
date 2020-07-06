package j130.netchat.server;

import j130.netchat.MyEventListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class ServerSocketListener implements Runnable{

    private final int port;
    private static volatile LinkedList<ServerReplyTask> serverReplyTaskList = new LinkedList<>();
    private static volatile Queue<String> queueReply = new LinkedList<>();
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static MyEventListener listener;
    
    public ServerSocketListener() {
        this.port = 29321;
    }

    public ServerSocketListener(int port) {
        this.port = port;
    }
    
    public static String getTime(){
        return timeFormatter.format(LocalTime.now());
    }

    public static String getUserInput() {
        return queueReply.poll();
    }

    public static void addUserInput(String userInput) {
        queueReply.add(userInput);
        fireDataChanged("updateUserInput");
    }

    public static LinkedList<ServerReplyTask> getServerReplyTaskList() {
        return new LinkedList<>(serverReplyTaskList);
    }
    
    public static void removeServerReplyTask(ServerReplyTask serverReplyTask) {
        serverReplyTaskList.remove(serverReplyTask);
        fireDataChanged("updateUserList");
    }
    
    public static void addListener(MyEventListener listener){
        if (ServerSocketListener.listener == null) {
            ServerSocketListener.listener = listener;
        }
    } 
    
    public static void removeListener(){
        ServerSocketListener.listener = null;
    }
    
    public static void fireDataChanged(String message){                
        if (listener != null){
            listener.update(message);
        }
    }
    
    @Override
    public void run() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
        ) {
            System.out.println("Server started");
            while (true){
                Socket clientSocket = serverSocket.accept();
                ServerReplyTask serverReplyTask = new ServerReplyTask(clientSocket);
                Thread serverReplyThread = new Thread(serverReplyTask,"serverReplyTask");
                serverReplyThread.start();
                serverReplyTaskList.add(serverReplyTask);
            }
        } catch (IOException ex) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(ex.getMessage());
            
        }
    }
}

