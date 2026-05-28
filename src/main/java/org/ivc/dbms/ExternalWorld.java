package org.ivc.dbms;
import java.sql.Connection;
import java.util.Scanner;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExternalWorld {
    public static void runInterface(Connection connection){
        try{
            try (Scanner scanner = new Scanner(System.in)) {
                UtilsDAO.resetDatabase(connection);
                ProductLoader.loadProducts(connection, "data/StarterData.xlsx");
                displayOptions();
                int input = readPositiveInt(scanner, "");
                while(input != 6){
                    switch (input) {
                        case 1 -> {
                            readShipmentNotice(connection, scanner);
                        }
                        case 2 -> {
                            readShipmentDelivery(connection, scanner);
                        }
                        case 3 -> {
                            checkQuantity(connection, scanner);
                        }
                        case 4 -> {
                            ProductDAO.printProducts(connection);
                        }
                        case 5 -> {
                            ShipmentDAO.printShipmentNotices(connection);
                        }
                        default -> {
                            System.out.println("PLEASE ENTER A VALID INPUT");
                        }
                    }
                    displayOptions();
                    input = readPositiveInt(scanner, "");
                }
            }
        }catch(Exception e){
            System.out.println("ERROR DISPLAYING PRODUCTS AND SHIPPING NOTICES"); 
            System.out.print(e);
        }
    }


    public static void displayOptions(){
        System.out.println();
        System.out.println("OPTIONS: [1] SHIPPING NOTICE  [2] DELIVER SHIPMENT  [3] CHECK QTY  [4] PRINT PRODUCTS  [5] PRINT NOTICES  [6] QUIT");
        System.out.print("ENTER AN OPTION: ");
    }

    public static void checkQuantity(Connection connection, Scanner scanner) throws SQLException{
        while (true) {
            System.out.print("ENTER A STOCK NUM: ");
            String stockNum = scanner.nextLine().trim();
    
            try{
                int quantity = ProductDAO.getQuantity(connection, stockNum);
                System.out.println("QUANTITY: " + quantity);
                return;
            }catch (SQLException e){
                System.out.println("PLEASE ENTER A VALID STOCK NUM");
            }
        }
    }
   
    public static String readNoticeID(Connection connection, Scanner scanner, String prompt) throws SQLException{
        String noticeID; 
        while (true) {
            noticeID = readNonEmptyString(scanner, prompt);
            if(ShipmentDAO.isNewNoticeID(connection, noticeID)){
                return noticeID;
            }
            else{
                System.out.println("NOTICE ID " + noticeID + " WAS ALREADY USED");
            }
        }
    }

    public static String readLocation(Scanner scanner, String prompt){
        while (true) {
            System.out.print(prompt);
            String locationID = scanner.nextLine().trim();
    
            if (locationID.matches("[A-Za-z](0|[1-9][0-9]*)")) {
                return locationID.toUpperCase();
            }
    
            System.out.println(
                "Improper Location Formatting."
            );
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
    
            try {
                int value = Integer.parseInt(input);
    
                if (value >= 0) {
                    return value;
                }
    
                System.out.println("VALUE MUST BE POSITIVE.");
            } catch (NumberFormatException e) {
                System.out.println("PLEASE ENTER A VALID INTEGER.");
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
    
            if (!input.isBlank()) {
                return input;
            }
    
            System.out.println("INPUT CANNOT BE EMPTY.");
        }
    }

    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();
    
            if (input.equals("y")|| input.equals("Y") || input.equals("yes")) {
                return true;
            }
    
            if (input.equals("n") || input.equals ("N") || input.equals("no")) {
                return false;
            }
    
            System.out.println("PLEASE ENTER Y OR N.");
        }
    }

    public static void printShipmentInfo(String noticeID, String carrier, List<Item> shipmentItems) {
        System.out.println();
        System.out.println("NOTICE ID: " + noticeID);
        System.out.println("CARRIER:   " + carrier);
        System.out.println("SHIPMENT ITEMS:");
        System.out.printf("%-20s %-20s %10s%n", "MANUFACTURER", "MODEL NUMBER", "QUANTITY");
        System.out.println("------------------------------------------------------");
    
        for (Item shipmentItem : shipmentItems) {
            System.out.printf(
                "%-20s %-20s %10d%n",
                shipmentItem.getManufacturer(),
                shipmentItem.getModelNumber(),
                shipmentItem.getQuantity()
            );
        }
    }

    public static void readShipmentNotice(Connection connection, Scanner scanner) throws Exception{
        String stockNum; 
        String manufacturer; 
        String modelNumber; 
        String location; 
        int minStockLevel; 
        int maxStockLevel; 
        int shipQuantity;

        String newProdStockNum = "0";

        List<Product> newProducts = new ArrayList<>(); 
        List<Item> shipmentItems = new ArrayList<>(); 

        boolean keepAddingItems;

        String noticeID = readNoticeID(connection, scanner,  "ENTER A NOTICE ID: ");
        if(noticeID.equals("-1")){return;}
        String carrier = readNonEmptyString(scanner, "ENTER A CARRIER: ");
        if(carrier.equals("-1")){return;}

        do{
            manufacturer = readNonEmptyString(scanner, "ENTER A MANUFACTURER: ");
            modelNumber = readNonEmptyString(scanner, "ENTER A MODEL NUMBER: ");
            shipQuantity = readPositiveInt(scanner, "ENTER QUANTITY SHIPPED: ");
            try{
                stockNum = ProductDAO.getStockNum(connection, manufacturer, modelNumber);

            }catch(SQLException e){
                if(newProducts.isEmpty()){
                    stockNum = ProductDAO.newStockNum(connection);
                    newProdStockNum = stockNum;
                }else{
                    stockNum = ProductDAO.incrementStockNum(newProdStockNum);
                }
                location = readLocation(scanner, "ENTER A LOCATION E.G. (A6): ");
                minStockLevel = readPositiveInt(scanner, "ENTER A MIN STOCK LEVEL: ");
                maxStockLevel = readPositiveInt(scanner, "ENTER A MAX STOCK LEVEL: ");
                Product newProduct = new Product(stockNum, location, manufacturer, modelNumber, 0, minStockLevel, maxStockLevel, 0);
                newProducts.add(newProduct);
            }
            Item shipmentItem = new Item(stockNum, shipQuantity); 
            shipmentItem.setManufacturer(manufacturer);
            shipmentItem.setModelNumber(modelNumber);

            shipmentItems.add(shipmentItem);
            keepAddingItems = readYesNo(scanner, "ENTER MORE ITEMS");
        }while(keepAddingItems == true);


        printShipmentInfo(noticeID, carrier, shipmentItems);
        if(!readYesNo(scanner, "CONFIRM SHIPPING NOTICE")){
            System.out.println("SHIPPING NOTICE CANCELLED");
            return;
        }

        System.out.println("SHIPPING NOTICE " + noticeID + " HAS BEEN CONFIRMED");
        for (Product newProduct : newProducts){
            ProductDAO.addProduct(connection, newProduct);
        }
        
        ShipmentDAO.processShipNotice(connection, noticeID, carrier, shipmentItems);

    }

    public static void readShipmentDelivery(Connection connection, Scanner scanner){
        String noticeID = "SOMETHING";
        try{
            noticeID = readNonEmptyString(scanner, "ENTER AN EXISTING NOTICE ID: ");

            ShipmentDAO.receiveShipment(connection, noticeID);
    
        }catch(SQLException e){
            System.out.println("NO SHIPMENT FOUND WITH NOTICE ID: " + noticeID);
        } 
        
    }

}
