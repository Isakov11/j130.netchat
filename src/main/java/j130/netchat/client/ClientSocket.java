package j130.netchat.client;

import j130.netchat.MyEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ClientSocket implements Runnable, Monitor {

    private String host;
    private volatile String login;
    private Socket echoSocket;
    private PrintWriter out;
    private BufferedReader inputBufferedReader;
    private volatile Queue<String> serverReply;
    private MyEventListener listener;
    private final int port;
    private final Object monitor = new Object();
    private boolean logon;
    private boolean alive;
    private volatile boolean clientReady;

    public ClientSocket(int port) {
        this.port = port;
        serverReply = new LinkedList<>();
        logon = false;
        alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void logon(String host, String login) {
        try {
            if (this.host == null){
                this.host = host;
            }
            logon = true;
            clientReady = true;

            initConnection();
            
            synchronized(getMonitor()){
                getMonitor().notify();
            }
            
            //this.login = login;
            sendUserInput("@logon " + login);
        } catch (IOException ex) {
            System.out.println("init error + " + ex);
        }
    }
    
    @Override
    public void sendUserInput(String UserInput){
        if (UserInput != null){
            out.println(UserInput);
        }
    }
    
    @Override
    public String getServerReply() {
        return serverReply.poll();
    }

    @Override
    public Object getMonitor() {
        return monitor;
    }
    
    @Override
    public void addListener(MyEventListener listener){
        if (this.listener == null) {
            this.listener = listener;
        }
    } 
    
    @Override
    public void removeListener(){
        this.listener = null;
    }
    
    private void fireDataChanged(String message){                
        listener.update(message);
    }
    
    private void waitForHost(){
        System.out.println("waitForHost in ");
        synchronized(monitor){
            try {
                while (host == null){
                    monitor.wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException");
            }
        }
        System.out.println("waitForHost -- out");
    }

    private void initConnection() throws IOException{
        System.out.println("initConnection true");
        echoSocket = new Socket(host, port);
        inputBufferedReader = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        out = new PrintWriter(echoSocket.getOutputStream(), true);
    }
    
    private String checkServerLogonReply(String inputLine) {
        if (logon){
            if(inputLine.subSequence(0, 3).equals("@CE")){
                logon = false;
                this.login = (String) inputLine.subSequence(4, inputLine.length());
                inputLine = "Welcome " + login;
                System.out.println("login accepted");
                
            }
            else{
                logon = false;
                clientReady = false;
                inputLine = "Login " + login + " is busy";
            }
        }
        if(inputLine.subSequence(0, 1).equals("@") && !logon){
            if(inputLine.subSequence(0, 3).equals("@LA")){
                inputLine = "New login " + inputLine.subSequence(4, inputLine.length()) + " was set";
                login = (String) inputLine.subSequence(4, inputLine.length());
                
            }
            if(inputLine.subSequence(0, 3).equals("@LR")){
                inputLine = "Login " + inputLine.subSequence(4, inputLine.length()) + " is busy";
            }
        }
        return inputLine;
    }
    
    @Override
    public void run() {
        System.out.println("Client running");
        while (alive){
            waitForHost();
            try{  
                //Получение ответа с сервера
                //------------------------------------------------------------------
                String inputLine;
                while (clientReady){
                    try {
                        sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("Client sleep interrupted");
                    }
                    if (inputBufferedReader.ready()) {
                        inputLine = inputBufferedReader.readLine();
                        System.out.println("Server reply: " + inputLine);
                        
                        inputLine = checkServerLogonReply(inputLine);
                        
                        serverReply.add(inputLine);
                        fireDataChanged("update");
                    }
                }
            } 
            catch (UnknownHostException e) {
                System.out.println("Client Error Don't know about host " + host);
            } 
            catch (IOException e) {
                System.out.println("Client Error Couldn't get I/O for the connection to " +
                        host);
                System.out.println(e);
            }
            finally{
                System.out.println("Client finally block");
                try {
                    if (echoSocket != null){
                        sendUserInput("@CC");
                        clientReady = false;
                        inputBufferedReader.close();
                        out.close();
                        echoSocket.close();
                    }
                    host = null;
                    echoSocket = null;
                    fireDataChanged("CR");
                    System.out.println("-------------------------------------");
                } catch (IOException ex) {
                    System.out.println("Client finally block error: " + ex);
                }
                
            }
        }
    } 
}
