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
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 *
 * @author alingasada_sd2021
 */
public class Client extends JFrame{

    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message;
    private String chatserver;
    private Socket client;
    
    public Client(String host) {
        super("Client");
        this.chatserver =  host;
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
    
    public void runClient(){
                try // connect to server, get streams, process connection
         {
          connectToServer(); // create a Socket to make connection
          getStreams(); // get the input and output streams
          processConnection(); // process connection
          } // end try
          catch ( EOFException eofException )
          {
          displayMessage( "\nClient terminated connection" );
          } // end catch
          catch ( IOException ioException )
          {
          ioException.printStackTrace();
          } // end catch
          finally
          {
          closeConnection();
          }
    }

    private void connectToServer() throws IOException{
        displayMessage("Attempting connection\n");
        client = new Socket(InetAddress.getByName(chatserver),12345);
        displayMessage("Connected to: "+ client.getInetAddress().getHostName());
    }

    private void getStreams()throws IOException {
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();
        
        input = new ObjectInputStream(client.getInputStream());
        displayMessage("\nGot I/O streams\n");
    }

    private void processConnection()throws IOException {
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
            
        }while(!message.equals("SERVER>>> TERMINATE"));
    }
    
    private void closeConnection() {
       displayMessage("\nTerminating Connection\n");
        setTextFieldEditable(false);
        try{
            output.close();
            input.close();
            client.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    
     private void sendData(String message) {
       try{
           output.writeObject("CLIENT>>> "+ message);
           output.flush();
           displayMessage("\nCLIENT>>> "+ message);
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
       

       Client client = new Client("127.0.0.1");
       client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       client.setLocationRelativeTo(null);
       client.runClient();
   }
}
