package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.util.List;

public class ReceiptGenerator {
    public static String generate(Order o) {

        StringBuilder sb = new StringBuilder();

        for(OrderItem i : o.getItems()) {

            Product p = i.getProduct();

            sb.append(p.getNume())
                    .append(": ")
                    .append(p.getPret())
                    .append(" x ")
                    .append(i.getQuantity())
                    .append(" = ")
                    .append(i.getTotal())
                    .append(" RON\n");
        }

        return sb.toString();
    }
}