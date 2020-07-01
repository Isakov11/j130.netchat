/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.net.client;

/**
 *
 * @author Hino
 */
public interface Monitor {
    
    public String getUserInput();
    
    public void setUserInput(String userInput);
    
    public Object getMonitor();
    
    public String getServerReply();
}
