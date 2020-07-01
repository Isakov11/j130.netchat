/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.net.client;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author Hino
 */
public class UserInputTask implements Runnable{
    private final OutputStream outputStream;
    private final Monitor monitor;
    
    public UserInputTask(OutputStream outputStream, Monitor monitor) {
        this.outputStream = outputStream;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        synchronized(monitor.getMonitor()){
            String userInput;
            try (PrintWriter out = new PrintWriter(outputStream, true)){
                while (true){
                    userInput = monitor.getUserInput();
                    if (userInput == null) {
                        try {
                            monitor.getMonitor().wait();
                        } catch (InterruptedException ex) {
                            System.out.println("InterruptedException");
                        }
                    }
                    else{
                        out.println(userInput);
                        //System.out.println("echo: " + userInput);
                        userInput = null;
                    }
                }
            }
        }
    }
}
