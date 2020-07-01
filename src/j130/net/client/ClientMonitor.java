/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.net.client;


import j130.net.MyEventListener;
import java.util.ArrayList;

public class ClientMonitor {
    private String host;
    private volatile String userInput;
    private volatile String serverReply;
    private transient ArrayList<MyEventListener> listeners = new ArrayList<>();
    private static final ClientMonitor instance = new ClientMonitor();
    
    synchronized public static ClientMonitor getInstance() { return instance; }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
    
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
    
    public String getUserInput() {
        String temp = userInput;
        userInput = null;
        return temp;
    }

    public String getServerReply() {
        String temp = serverReply;
        serverReply = null;
        return temp;
    }

    public void setServerReply(String serverReply) {
        this.serverReply = serverReply;
        fireDataChanged("update");
    }
   
    public void addListener(MyEventListener listener){
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    } 
    
    public void removeListener(MyEventListener listener){
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }
    
    public void removeAllListeners() {
        listeners.clear();
    }
    
    public MyEventListener[] getListeners(){
        return listeners.toArray(new MyEventListener[listeners.size()]);
    }
    
    public void fireDataChanged(String message){                
        listeners.forEach((listener) -> {
            listener.update(message);
        });
    }
    
}
