package org.ivc.dbms;
import java.net.*;
import java.io.*;

public class Server {
    // Initialize socket and input stream
    private Socket s;
    private ServerSocket ss;
    private DataInputStream in;

    // Constructor with port
    public Server(int port) {
      
        // Starts server and waits for a connection
        try
        {
            ss = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            s = ss.accept();
            System.out.println("Client accepted");

            // Takes input from the client socket
            in = new DataInputStream(
                new BufferedInputStream(s.getInputStream()));

            String request = "";
            int numItems;
            String stockNum;
            int quantity; 

            // Reads message from client until "Over" is sent
            while (!request.equals("Over"))
            {
                try
                {
                    request = in.readUTF();
                    if(request.equals("ORDER")){
                        System.out.println("ORDER PROCESSING");
                        numItems = Integer.parseInt(in.readUTF());
                        for(int i = 0; i < numItems; i++){
                            stockNum = in.readUTF();
                            quantity = Integer.parseInt(in.readUTF());
                            System.out.println("ITEM " + (i+1) + " STOCK NUMBER: " + stockNum + " QUANTITY: " + quantity);
                        }

                    }
                    else if (request.equals("INVENTORY")){
                        System.out.println("INVENTORY REQUEST"); 
                    }
                    

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // Close connection
            s.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
}