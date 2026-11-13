
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

enum OrderStatus {
    DRAFT,
    PLACED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

class OrderLine {
    private final String productId;
    private final String productName;
    private double price;
    private int quantity;

    public OrderLine(String productId, String productName,
                     double price, int quantity) {
        if (price < 0 || quantity <= 0) {
            throw new IllegalArgumentException("Invalid price or quantity");
        }

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public double getLineTotal() {
        return price * quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        quantity += amount;
    }

    @Override
    public String toString() {
        return productName + " | Quantity: " + quantity
                + " | Line Total: Rs. " + getLineTotal();
    }
}

class Order {
    private final String orderId;
    private final Map<String, OrderLine> lines = new HashMap<>();

    private OrderStatus status = OrderStatus.DRAFT;
    private LocalDateTime shippedAt;
    private String trackingNumber;

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public void addLine(String productId, String productName,
                        double price, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Lines can only be added in DRAFT state");
        }

        if (lines.containsKey(productId)) {
            lines.get(productId).increaseQuantity(quantity);
        } else {
            lines.put(productId,
                    new OrderLine(productId, productName, price, quantity));
        }
    }

    public void removeLine(String productId) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Lines can only be removed in DRAFT state");
        }

        if (lines.remove(productId) == null) {
            throw new IllegalArgumentException("Product not found");
        }
    }

    public double getTotal() {
        double total = 0;

        for (OrderLine line : lines.values()) {
            total += line.getLineTotal();
        }

        return total;
    }

    public void place() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT orders can be placed");
        }

        if (lines.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot place an order with no lines");
        }

        status = OrderStatus.PLACED;
        System.out.println("Order placed successfully.");
    }

    public void pay() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Only PLACED orders can be paid");
        }

        status = OrderStatus.PAID;
        System.out.println("Payment completed successfully.");
    }

    public void ship(String trackingNumber) {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Only paid orders can be shipped");
        }

        if (lines.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot ship an order with no lines");
        }

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number cannot be empty");
        }

        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
        this.trackingNumber = trackingNumber;

        System.out.println("Order shipped successfully.");
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                    "Only SHIPPED orders can be delivered");
        }

        status = OrderStatus.DELIVERED;
        System.out.println("Order delivered successfully.");
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED
                || status == OrderStatus.DELIVERED
                || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "This order cannot be cancelled");
        }

        status = OrderStatus.CANCELLED;
        System.out.println("Order cancelled successfully.");
    }

    public void displayOrder() {
        System.out.println("\n----- ORDER DETAILS -----");
        System.out.println("Order ID: " + orderId);
        System.out.println("Status: " + status);

        System.out.println("Order Lines:");
        for (OrderLine line : lines.values()) {
            System.out.println(line);
        }

        System.out.println("Total: Rs. " + getTotal());
        System.out.println("Tracking Number: " + trackingNumber);
        System.out.println("Shipped At: " + shippedAt);
        System.out.println("-------------------------");
    }
}

public class OrderLifecycle {
    public static void main(String[] args) {

        Order order = new Order("ORD101");

        try {
            // Adding products
            order.addLine("P101", "Laptop", 50000, 1);
            order.addLine("P102", "Mouse", 1000, 2);

            // Adding the same product increases quantity
            order.addLine("P102", "Mouse", 1000, 1);

            order.displayOrder();

            // Order lifecycle
            order.place();
            order.pay();
            order.ship("TRK123456");
            order.deliver();

            // Display final order
            order.displayOrder();

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
