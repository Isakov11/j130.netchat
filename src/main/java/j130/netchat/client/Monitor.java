/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.netchat.client;

import j130.netchat.MyEventListener;

/**
 *
 * @author Hino
 */
public interface Monitor {
    
    public Object getMonitor();
    
    public void logon(String host, String login);
    
    public void sendUserInput(String UserInput);
    
    public String getServerReply();
    
    public void setAlive(boolean alive);
    
    public void addListener(MyEventListener listener);
    
    public void removeListener();
    
}
