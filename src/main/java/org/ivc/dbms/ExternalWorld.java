package org.ivc.dbms;
import java.sql.Connection;
import java.util.Scanner;
import java.util.List;

public class ExternalWorld {
    public static void runInterface(Connection connection){
        try{
            try (Scanner scanner = new Scanner(System.in)) {
                UtilsDAO.resetDatabase(connection);
                ProductLoader.loadProducts(connection, "data/StarterData.xlsx");
                displayState(connection);
                int input = scanner.nextInt();
                scanner.nextLine();
                while(input != 3){
                    switch (input) {
                        case 1 -> {
                            readShipmentNotice(connection, scanner);
                        }
                        case 2 -> {
                            readShipmentDelivery(connection, scanner);
                        }
                        default -> {
                            System.out.println("PLEASE ENTER A VALID INPUT");
                        }
                    }
                    displayState(connection);
                    input = scanner.nextInt();
                    scanner.nextLine(); 
                }
            }
        }catch(Exception e){
            System.out.println("ERROR DISPLAYING PRODUCTS"); 
        }
    }

    public static void displayState(Connection connection) throws Exception{
        ProductDAO.printProducts(connection);
        displayOptions(); 
    }

    public static void displayOptions(){
        System.out.println("OPTIONS:");
        System.out.println("1: DELIVER SHIPPIGN NOTICE");
        System.out.println("2: DELIVER SHIPMENT");
        System.out.println("3: END INTERFACE");
        System.out.print("ENTER OPTION: ");
    }
    public static String readNoticeID(Scanner scanner, String prompt) throws Exception{
        System.out.print(prompt);
        String noticeID = scanner.nextLine();
        return noticeID;
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
    
            try {
                int value = Integer.parseInt(input);
    
                if (value > 0) {
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


    public static void readShipmentNotice(Connection connection, Scanner scanner) throws Exception{
        String manufacturer = ""; 
        String modelNumber = ""; 
        int shipQuantity = 0;
        boolean keepAddingItems = true;

        String noticeID = readNoticeID(scanner, "ENTER A NOTICE ID");
        String carrier = readNonEmptyString(scanner, "ENTER A CARRIER: ");

        do{
            manufacturer = readNonEmptyString(scanner, "ENTER A MANUFACTURER: ");
            modelNumber = readNonEmptyString(scanner, "ENTER A MODEL NUMBER: ");
            shipQuantity = readPositiveInt(scanner, "ENTER QUANTITY SHIPPED: ");
            keepAddingItems = readYesNo(scanner, "ENTER MORE ITEMS");
        }while(keepAddingItems == true);

    }

    public static void readShipmentDelivery(Connection connection, Scanner scanner) throws Exception{

    }

}
