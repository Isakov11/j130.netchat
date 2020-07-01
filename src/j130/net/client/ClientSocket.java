package j130.net.client;

import j130.net.MyEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ClientSocket implements Runnable, Monitor {

    private volatile String host;
    private volatile String login;
    /*private volatile Socket echoSocket;
    private volatile OutputStream outputStream;
    private volatile BufferedReader in;*/
    private volatile Queue<String> userInput;
    private volatile Queue<String> serverReply;
    private MyEventListener listener;
    private final int port;
    private final Object monitor = new Object();
    private boolean logonFlag;

    public ClientSocket(int port) {
        this.port = port;
        userInput = new LinkedList<>();
        serverReply = new LinkedList<>();
        logonFlag = false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (this.host == null){
            this.host = host;
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
        setUserInput("@login " + login);
    }
    
    public void logon(String login) {
        logonFlag = true;
        this.login = login;
        setUserInput("@logon " + login);
    }
    
    @Override
    public void setUserInput(String userInput) {
        this.userInput.add(userInput);
    }

    @Override
    public String getUserInput() {
        return userInput.poll();
    }

    @Override
    public String getServerReply() {
        return serverReply.poll();
    }

    @Override
    public Object getMonitor() {
        return monitor;
    }
    
    public void addListener(MyEventListener listener){
        if (this.listener == null) {
            this.listener = listener;
        }
    } 
    
    public void removeListener(){
        this.listener = null;
    }
    
    private void fireDataChanged(String message){                
        listener.update(message);
    }
    
    @Override
    public void run() {
    synchronized(monitor){
        try {
            while (host == null){
                monitor.wait();
            }
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException");
        }
    }
        try (Socket echoSocket = new Socket(host, port);
                OutputStream outputStream = echoSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))) {
            /*if (logonFlag) {
                Socket echoSocket = new Socket(host, port);
                outputStream = echoSocket.getOutputStream();
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));*/

                //Запуск потока отправки пользователького ввода
                //------------------------------------------------------------------
                UserInputTask userInputTask = new UserInputTask(outputStream, this);
                Thread userInputThread = new Thread(userInputTask,"userInputTask");
                userInputThread.start();
                //------------------------------------------------------------------
                
            //}
            
            //Получение ответа с сервера
            //------------------------------------------------------------------
            String inputLine;
            while (true){
                if ((inputLine = in.readLine()) != null) {
                    //System.out.println("Client get from server: " + inputLine);
                    /*if (logonFlag  == true && inputLine.contains("@CR")){
                        echoSocket.close();
                        fireDataChanged("CR");
                        logonFlag = false;
                    }
                    else{*/
                        serverReply.add(inputLine);
                        fireDataChanged("update");
                    //}
                }
            }
            //------------------------------------------------------------------
        } 
        catch (UnknownHostException e) {
            System.out.println("Client Error Don't know about host " + host);
        } catch (IOException e) {
            System.out.println("Client Error Couldn't get I/O for the connection to " +
                    host);
        }
        /*finally{
        try {
            System.out.println("finally block try to close");
            echoSocket.close();
            
        } catch (IOException ex) {
            System.out.println("Client Error Don't know about host " + host);
        }
        }*/
    } 
}
