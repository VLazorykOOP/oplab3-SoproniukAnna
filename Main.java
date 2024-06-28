import java.util.ArrayList;
import java.util.List;

// Шаблон Builder для створення замовлення
class Order {
    private String customerName;
    private List<Item> items;
    private double totalCost;

    private Order(OrderBuilder builder) {
        this.customerName = builder.customerName;
        this.items = builder.items;
        this.totalCost = builder.totalCost;
    }

    public static class OrderBuilder {
        private String customerName;
        private List<Item> items = new ArrayList<>();
        private double totalCost = 0;

        public OrderBuilder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderBuilder addItem(Item item) {
            this.items.add(item);
            this.totalCost += item.getPrice();
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "customerName='" + customerName + '\'' +
                ", items=" + items +
                ", totalCost=" + totalCost +
                '}';
    }
}

// Шаблон Composite для створення комплексного замовлення
interface Item {
    double getPrice();
    String getName();
}

class Dish implements Item {
    private String name;
    private double price;

    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}

class CompositeItem implements Item {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    @Override
    public double getPrice() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public String getName() {
        StringBuilder names = new StringBuilder();
        for (Item item : items) {
            names.append(item.getName()).append(", ");
        }
        return names.toString();
    }

    @Override
    public String toString() {
        return "CompositeItem{" +
                "items=" + items +
                '}';
    }
}

// Шаблон Chain of Responsibility для обробки замовлення
abstract class OrderHandler {
    protected OrderHandler nextHandler;

    public void setNextHandler(OrderHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void handleOrder(Order order) {
        processOrder(order);
        if (nextHandler != null) {
            nextHandler.handleOrder(order);
        }
    }

    protected abstract void processOrder(Order order);
}

class ReceiveOrderHandler extends OrderHandler {
    @Override
    protected void processOrder(Order order) {
        System.out.println("Order received: " + order);
    }
}

class PrepareOrderHandler extends OrderHandler {
    @Override
    protected void processOrder(Order order) {
        System.out.println("Order is being prepared: " + order);
    }
}

class DeliverOrderHandler extends OrderHandler {
    @Override
    protected void processOrder(Order order) {
        System.out.println("Order is being delivered: " + order);
    }
}

// Основний клас для демонстрації роботи системи
public class Main {
    public static void main(String[] args) {
        // Створення страв
        Item pizza = new Dish("Pizza", 8.99);
        Item burger = new Dish("Burger", 5.99);
        Item cola = new Dish("Cola", 1.99);

        // Створення комплексного замовлення
        CompositeItem combo = new CompositeItem();
        combo.addItem(pizza);
        combo.addItem(cola);

        // Створення замовлення з використанням Builder
        Order order = new Order.OrderBuilder()
                .setCustomerName("John Doe")
                .addItem(burger)
                .addItem(combo)
                .build();

        // Створення ланцюжка обробки замовлення
        OrderHandler receiveHandler = new ReceiveOrderHandler();
        OrderHandler prepareHandler = new PrepareOrderHandler();
        OrderHandler deliverHandler = new DeliverOrderHandler();

        receiveHandler.setNextHandler(prepareHandler);
        prepareHandler.setNextHandler(deliverHandler);

        // Обробка замовлення
        receiveHandler.handleOrder(order);
    }
}
