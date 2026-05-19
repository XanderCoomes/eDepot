package org.ivc.dbms;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ProductLoader {

    public static void loadProducts(Connection connection, String excelFilePath) throws Exception {
        String sql = """
            INSERT INTO products (
                stock_num,
                location_id,
                manufacturer,
                model_number,
                quantity,
                min_stock_level,
                max_stock_level,
                replenishment
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
            FileInputStream file = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(file);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            int count = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String stockNum = getString(row.getCell(0));

                // Only import actual product rows.
                // This skips "Table 1", headers, blank rows, continuation description rows,
                // and the customer data later in the sheet.
                if (stockNum == null) {
                    continue;
                }

                if("ID".equals(stockNum)){
                    break;
                }

                String manufacturer = getString(row.getCell(2));  // MANU
                String modelNumber = getString(row.getCell(3));   // MODEL#
                int minStock = getInt(row.getCell(8));            // Min
                int quantity = getInt(row.getCell(9));            // Qty
                int maxStock = getInt(row.getCell(10));           // Max
                String locationId = getString(row.getCell(11));   // Location

                int replenishment = 0;

                statement.setString(1, stockNum);
                statement.setString(2, locationId);
                statement.setString(3, manufacturer);
                statement.setString(4, modelNumber);
                statement.setInt(5, quantity);
                statement.setInt(6, minStock);
                statement.setInt(7, maxStock);
                statement.setInt(8, replenishment);

                statement.executeUpdate();
                count++;
            }

            System.out.println("Imported " + count + " products.");
        }
    }

    private static String getString(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell).trim();

        if (value.isEmpty()) {
            return null;
        }

        return value;
    }

    private static int getInt(Cell cell) {
        if (cell == null) {
            return 0;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell).trim();

        if (value.isEmpty()) {
            return 0;
        }

        value = value.replace(",", "");

        return Integer.parseInt(value);
    }
}