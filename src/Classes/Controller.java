/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Guis.ServerApp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextField;

/**
 *
 * @author alingasada_sd2021
 */
public class Controller {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    private int counter;
    private ServerApp servApp;
    private JTextField messageField;
    
    public Controller() {
        initComponents();
        runServer();
    }

    private void initComponents() {
        servApp = new ServerApp();
        servApp.setVisible(true);
        servApp.getMessageField().setEditable(false);
        this.messageField = servApp.getMessageField();
        servApp.getBut_StartServer().addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                servApp.getMessageField().setEditable(true);
                servApp.getStatus_Label().setText("WAITING FOR CONNECTION....");
                servApp.getMessageField().addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(counter>0){
                         sendData(e.getActionCommand());
                         servApp.getMessageField().setText("");
                        }else{
                          servApp.getMessageField().setText("");
                        }
                    }
                });
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        
        });
        
        
        
    }
    public void runServer(){
        try{
            server = new ServerSocket(12345,3000);
            while(true){
                try
                    {
                     waitForConnection(); // wait for a connection
                     getStreams(); // get input & output streams
                     processConnection(); // process connection
                    } // end try
                     catch ( EOFException eofException )
                     {
                    displayMessage( "\nServer terminated connection" );
                     } // end catch
                     finally
                     {
                    closeConnection(); // close connection
                    ++counter;
                     } // end finally
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

     private void waitForConnection() throws IOException {
        displayMessage("Waiting for connection..");
        connection = server.accept();
        displayMessage("Connection "+counter+" received from: " + connection.getInetAddress().getHostName());
        
    }

     private void getStreams()throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        
        input = new ObjectInputStream(connection.getInputStream());
        
        displayMessage("Got I/O Streams\n");
    }

    private void processConnection() throws IOException{
        String message = "Connection Successful";
        sendData(message);
        this.messageField.setEditable(true);
        
        do{
            try{
                message = (String) input.readObject();
                displayMessage("\n"+message);
            }catch(ClassNotFoundException classNotFoundException){
               displayMessage( "\nUnknown object type received" ); 
            }
            
        }while(!message.equals("ClIENT>>> TERMINATE"));
    }

    private void displayMessage(String message) {
        System.out.println(message);
    }
    
    private void sendData(String message) {
       try{
           output.writeObject("SERVER>>> "+ message);
           output.flush();
           displayMessage("\nSERVER>>> "+ message);
       }catch(IOException ioe){
         //  displayArea.append("\n Error writing object");
       }
    }

    private void closeConnection() {
        displayMessage("\nTerminating Connection\n");
       // setTextFieldEditable(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    
}
