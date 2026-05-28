package org.ivc.dbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ServerSocket ss;
    private final Connection connection;
    private final int port;

    public Server(int port, Connection connection) {
        this.port = port;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                System.out.println("Waiting for a client...");

                Socket s = ss.accept();
                System.out.println("Client accepted");

                ClientHandler handler = new ClientHandler(s, connection);
                handler.start();
            }

        } catch (IOException e) {
            System.out.println("Error in server connection: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket s;
        private final Connection connection;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket s, Connection connection) {
            this.s = s;
            this.connection = connection;
        }

        @Override
        public void run(){
            try {
                in = new DataInputStream(
                    new BufferedInputStream(s.getInputStream())
                );
                out = new DataOutputStream(
                    new BufferedOutputStream(s.getOutputStream())
                );

                String request = "";

                while (!request.equals("Over")) {
                    request = in.readUTF();
                    System.out.println(request);

                    if (request.equals("ORDER")) {
                        processOrder();
                    } 
                    else if (request.equals("INVENTORY")) {
                        processInventory();
                    }
                }

            } catch (Exception e) {
                System.out.println("Error in server connection: " + e.getMessage());

            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }

                    if (s != null) {
                        s.close();
                    }

                } catch (IOException e) {
                    System.out.println("Error in server connection: " + e.getMessage());
                }
            }
        }

        private void processOrder() throws Exception {
            System.out.println("ORDER PROCESSING");
            String stockNum;
            int quantity;

            List<Item> orderItems = new ArrayList<>();
            int numItems = Integer.parseInt(in.readUTF());

            for (int i = 0; i < numItems; i++) {
                stockNum = in.readUTF();
                quantity = Integer.parseInt(in.readUTF());

                System.out.println(
                    " STOCK NUMBER: " + stockNum +
                    " QUANTITY: " + quantity
                );
                Item orderItem = new Item(stockNum, quantity);
                orderItems.add(orderItem);
            }
            OrderDAO.processOrder(connection, orderItems);
            System.out.println("ORDER PROCESSED");
        }        

        private void processInventory() throws Exception {
            String sql = """
                SELECT stock_num, quantity
                FROM PRODUCTS
                ORDER BY stock_num
                """;

            try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

                List<Item> items = new ArrayList<>();

                while (resultSet.next()) {
                    String stockNum = resultSet.getString("stock_num");
                    int quantity = resultSet.getInt("quantity");

                    items.add(new Item(stockNum, quantity));
                }

                out.writeUTF(Integer.toString(items.size()));

                for (Item item : items) {
                    out.writeUTF(item.getStockNum());
                    out.writeUTF(Integer.toString(item.getQuantity()));
                }
                out.flush();
            }
        }
    }
}