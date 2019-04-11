/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author alingasada_sd2021
 */
public class Server extends JFrame{

    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    private int counter;
    
    public Server() {
        super("Server");
        this.enterField = new JTextField();
        this.enterField.setEditable(false);
        this.enterField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                enterField.setText("");
            }
        });
        
        add(enterField,BorderLayout.NORTH);
        displayArea = new JTextArea(); // create displayArea
        add( new JScrollPane( displayArea ), BorderLayout.CENTER );
        setSize( 300, 150 ); // set size of window
        setVisible( true );
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
        setTextFieldEditable(true);   
        do{
            try{
                message = (String) input.readObject();
                displayMessage("\n"+message);
            }catch(ClassNotFoundException classNotFoundException){
               displayMessage( "\nUnknown object type received" ); 
            }
            
        }while(!message.equals("ClIENT>>> TERMINATE"));
    }


    private void closeConnection() {
        displayMessage("\nTerminating Connection\n");
        setTextFieldEditable(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void sendData(String message) {
       try{
           output.writeObject("SERVER>>> "+ message);
           output.flush();
           displayMessage("\nSERVER>>> "+ message);
       }catch(IOException ioe){
           displayArea.append("\n Error writing object");
       }
    }
    
    private void displayMessage(final String message) {
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
               displayArea.append(message);
            }
        
        });
    }
    

    private void setTextFieldEditable(boolean b) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                enterField.setEditable(b);
            }
        });
    }
    public static void main(String args[]){
       
       Server server = new Server();
       server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       server.setLocationRelativeTo(null);
       server.runServer();

   }
}
