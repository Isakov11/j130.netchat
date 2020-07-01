/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j130.net.ui;

import j130.net.MyEventListener;
import j130.net.server.ServerReplyTask;
import j130.net.server.ServerSocketListener;
import javax.swing.DefaultListModel;


/**
 *
 * @author Hino
 */
public class ServerFrame extends javax.swing.JFrame  implements MyEventListener {

    /**
     * Creates new form Server
     */
    //ServerSocketListener serverSocket;
    private DefaultListModel<String> model = new DefaultListModel<>();
    
    public ServerFrame() {
        initComponents();
        //this.serverSocket = serverSocket;
        //model = new DefaultListModel<>();
        
        userList.setModel(model);
        ServerSocketListener.addListener((MyEventListener) this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        logTextArea.setRows(5);
        jScrollPane2.setViewportView(logTextArea);

        userList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(userList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ServerSocketListener.removeListener();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
         ServerSocketListener serverSocket = new ServerSocketListener(29321);
        Thread serverThread = new Thread(serverSocket,"serverSocket");
        serverThread.start();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame().setVisible(true);
            }
        });
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JList<String> userList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(String eventType) {
       
        if (eventType.equals("updateUserInput")){
            String userInput;
            while ( (userInput = ServerSocketListener.getUserInput()) != null){
                logTextArea.append(userInput + "\n");
            }
        }
        if (eventType.equals("updateUserList")){
            model.removeAllElements();
            for (ServerReplyTask replyTask : ServerSocketListener.getServerReplyTaskList()){
                model.addElement(replyTask.getClientLogin());
            }
        }
    }
}
