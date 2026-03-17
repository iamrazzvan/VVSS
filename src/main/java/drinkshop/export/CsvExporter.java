package drinkshop.export;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class CsvExporter {
    public static void exportOrders(List<Order> orders, String path) {
        try (FileWriter w = new FileWriter(path)) {
            w.write("OrderId,Product,Quantity,Price\n");
            double sum=0.0;
            for (Order o : orders){
                for (OrderItem i : o.getItems()) {
                    Product p = i.getProduct();
                    w.write(o.getId() + "," +
                            escapeCsv(p.getNume()) + "," +
                            i.getQuantity() + "," +
                            i.getTotal() + "\n");
                }
                w.write("total order: "+o.getTotal()+" RON\n");
                w.write("-------------------------------\n");
                sum+=o.getTotal();
            }
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            w.write("TOTAL OF "+date+" is: "+sum+" RON\n");
        } catch (IOException e) {
            System.err.println("Eroare la export CSV: " + e.getMessage());
            throw new RuntimeException("Export CSV failed", e);
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}