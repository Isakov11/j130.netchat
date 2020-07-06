/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.netchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerReplyTask implements Runnable {
    
    private final Socket clientSocket;
    private String clientLogin;
    private PrintWriter out;
    private StringBuilder stringBuilder;
    private boolean alive;
    
    public ServerReplyTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
        stringBuilder = new StringBuilder();
        alive = true;
    }

    public String getClientLogin() {
        return clientLogin;
    }
    
    private void broadcast(String userInput){
        String ip = clientSocket.getLocalAddress().getHostAddress();
        
        stringBuilder.append("[");
        stringBuilder.append(ServerSocketListener.getTime());
        stringBuilder.append("] ");
        stringBuilder.append(clientLogin);
        stringBuilder.append("(");
        stringBuilder.append(ip);
        stringBuilder.append("):\n");
        stringBuilder.append(userInput);
        
        for (ServerReplyTask replyTask : ServerSocketListener.getServerReplyTaskList()){
            replyTask.transmit( stringBuilder.toString());
        }
        ServerSocketListener.addUserInput(stringBuilder.toString());
        stringBuilder.setLength(0);
    }
    
    public void transmit(String userInput){
        out.println(userInput);
    }
    
    private boolean checkClientLogin(String login){
        boolean result = false;
        for (ServerReplyTask replyTask : ServerSocketListener.getServerReplyTaskList()){
            if (login.equals(replyTask.clientLogin)){
                result = true;
            }
        }
        return result;
    }
    
    private String checkControlSequence(String inputLine) throws IOException{
        if (inputLine.substring(0,1).equals("@")){
            System.out.println("ServerReplyTask get @ from client ");
            if (inputLine.substring(0,3).equals("@CC")){
                System.out.println("ServerReplyTask get @CC from client ");
                alive = false;
                clientSocket.close();
                ServerSocketListener.removeServerReplyTask(this);
                return null;
                
            }
            if (inputLine.substring(0,6).equals("@logon") && inputLine.length() > 7){
                System.out.print("ServerReplyTask get @logon from client ");
                String login = inputLine.substring(7);
                if(!checkClientLogin(login)){
                    System.out.println("@CE");
                    clientLogin = login;
                    //out.println("Welcome " + clientLogin);
                    out.println("@CE " + login);
                    ServerSocketListener.fireDataChanged("updateUserList");
                    return null;
                }
                else{
                    //out.println("Login " + login + " already in use");
                    alive = false;
                    out.println("@CR");
                    clientSocket.close();
                    ServerSocketListener.removeServerReplyTask(this);
                    System.out.println("@CR");
                    return null;
                }
            }
            if (inputLine.contains("@login") && inputLine.length() > 7){
                String login = inputLine.substring(7);
                if(!checkClientLogin(login)){
                    inputLine = clientLogin + " changed login at " + login;
                    clientLogin = login;
                    out.println("@LA " + login);
                    ServerSocketListener.fireDataChanged("updateUserList");
                    return inputLine;
                }
                else{
                    out.println("@LR " + login);
                    return null;
                }
            }
        }   
        return inputLine;
    }
    
    @Override
    public void run() {
        System.out.println("---------------------------------------");
        System.out.println("ServerReplyTask started");
        System.out.println("ServerReplyTasklist size " + ServerSocketListener.getServerReplyTaskList().size());
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())); ){
            
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("after init. alive " + alive);
        String inputLine;
        
        while(alive) {
            try {
                sleep(25);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerReplyTask.class.getName()).log(Level.SEVERE, null, ex);
            }
                inputLine = in.readLine();
                if (inputLine != null){
                    System.out.println("Client send " + inputLine);
                    inputLine = checkControlSequence(inputLine);
                    if (inputLine != null){
                        broadcast(inputLine);
                    }
                }
        }
        } catch (IOException ex) {
            System.out.println("Server Error: Exception caught when trying to listen on port "
                    + " or listening for a connection");
            System.out.println(ex.getMessage());
        }
        finally{
            System.out.println("ServerReplyTask finally block");
            try {
                out.close();
                clientSocket.close();
                ServerSocketListener.removeServerReplyTask(this);
            } catch (IOException ex1) {
                System.out.println("Server Error: Exception 2 caught when trying to listen on port "
                + " or listening for a connection");
            }
            System.out.println("ServerReplyTasklist size " + ServerSocketListener.getServerReplyTaskList().size());
            System.out.println("---------------------------------------");
        }
    }
}
