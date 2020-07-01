/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.net.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Hino
 */
public class ServerReplyTask implements Runnable {
    
    private final Socket clientSocket;
    private String clientLogin;
    private PrintWriter out;
    private StringBuilder stringBuilder;
    
    public ServerReplyTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
        stringBuilder = new StringBuilder();
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
    
    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        ) {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.substring(0,1).equals("@")){
                if (inputLine.contains("@logon") && inputLine.length() > 7){
                    String login = inputLine.substring(7);
                    if(!checkClientLogin(login)){
                        clientLogin = login;
                        out.println("Welcome " + clientLogin);
                        out.println("@CE");
                        ServerSocketListener.fireDataChanged("updateUserList");
                    }
                    /*else{
                        out.println("Login " + login + " already in use");
                        out.println("@CR");
                        clientSocket.close();
                    }*/
                }
                if (inputLine.contains("@login") && inputLine.length() > 7){
                    String login = inputLine.substring(7);
                    if(!checkClientLogin(login)){
                        clientLogin = login;
                        out.println("New login " + clientLogin + " was set");
                    }
                    else{
                        out.println("Login " + login + " already in use");
                        out.println("@LR");
                    }
                }
            }
            else{
                broadcast(inputLine);
            }
        }
        
        } catch (IOException ex) {
            System.out.println("Server Error: Exception caught when trying to listen on port "
                    + " or listening for a connection");
            System.out.println(ex.getMessage());
            try {
                clientSocket.close();
                ServerSocketListener.removeServerReplyTask(this);
            } catch (IOException ex1) {
                System.out.println("Server Error: Exception 2 caught when trying to listen on port "
                    + " or listening for a connection");
            }
        }finally{
            try {
                out.close();
                clientSocket.close();
                ServerSocketListener.removeServerReplyTask(this);
            } catch (IOException ex1) {
                System.out.println("Server Error: Exception 2 caught when trying to listen on port "
                + " or listening for a connection");
            }
        }
    }
}
