package org.example.server.system;

import org.example.server.people.Customer;
import org.example.server.people.Order;
import org.example.server.test.Food;

import java.util.*;

public class Store {
    private Map<String, Product> inventory;
    private Queue<Order> orderQueue;
    private Map<String, ArrayList<Order>> history;
    private Set<Customer> blockedCustomers;
    private static int totalOrders;
    List<Product> result = DataBase.jsonReader();

    public Store() {
        this.inventory = new HashMap<>();
        this.orderQueue = new LinkedList<>();
        this.history = new HashMap<>();
        this.blockedCustomers = new HashSet<>();
        load();
    }
    public void load() {
        inventory.clear();
        for (Product p : DataBase.jsonReader()) {
            inventory.put(p.getCode(), p);
        }
    }

    public void save() {
        DataBase.jsonWriter(new ArrayList<>(inventory.values()));
    }
    public void addProduct(Product p) {
        inventory.put(p.getCode(), p);
    }
    public void removeProduct(String code){
        inventory.remove(code);
    }

    public void processNextOrder() {
        Order order = orderQueue.poll();
        if (order == null) {
            System.out.println("Очередь заказов пуста.");
            return;
        }
        if ("CANCELLED".equals(order.getStatus())) {
            Customer c = order.getCustomer();
            c.incrementCancelledOrders();
            blockedCustomers.add(c);
            System.out.println("Покупатель " + c.getName());

            return;
        }
        for (Map.Entry<Product, Integer> e : order.getItems().entrySet()) {
            Product inStock = inventory.get(e.getKey().getCode());
            if (inStock == null || inStock.getQuantity() < e.getValue()) {
                order.setStatus("CANCELLED");
                Customer c = order.getCustomer();
                c.incrementCancelledOrders();
                blockedCustomers.add(c);

                System.out.println("Заказ " + order.getOrderId()
                        + " отменён: товара не хватает.");
                return;
            }
        }
        double total = 0;
        for (Map.Entry<Product, Integer> e : order.getItems().entrySet()) {
            Product inStock = inventory.get(e.getKey().getCode());
            inStock.setQuantity(inStock.getQuantity() - e.getValue());
            total += inStock.getPrice() * e.getValue();
        }


        order.setStatus("PROCESSED");
        System.out.printf("Заказ %s обработан. Сумма: %.2f%n",
                order.getOrderId(), total);
    }


    public List<Product> searchByName(String query) {
        if (query == null) return result;
        String q = query.toLowerCase();
        for (Product p : inventory.values()) {
            if (p.getName().toLowerCase().contains(q)) {
                result.add(p);
            }
        }
        return result;
    }

    public TreeMap<String, Integer> salesByCategory() {
    TreeMap<String, Integer> sales = new TreeMap<>();

    for (ArrayList<Order> orders : history.values()) {
        for (Order order : orders) {
            if (!"PROCESSED".equals(order.getStatus())) continue;

            for (Map.Entry<Product, Integer> e : order.getItems().entrySet()) {
                Product p =e.getKey();
                int qty = e.getValue();

                String category = switch (p.getClass().getSimpleName()) {
                    case "Clothing"    -> "Clothing";
                    case "Electronics" -> "Electronics";
                    case "Food"        -> "Food";
                    default            -> "Other";
                };

                sales.merge(category, qty, Integer::sum);
            }
        }
    }

    return sales;
}
    public String buildReceipt(Order order) {
        StringBuilder sb = new StringBuilder();


        sb.append("Заказ №: ").append(order.getOrderId()).append('\n');
        sb.append("Покупатель: ").append(order.getCustomer().getName())
                .append(" <").append(order.getCustomer().getEmail()).append(">\n");
        sb.append("Статус: ").append(order.getStatus()).append('\n');

        double total = 0;
        for (Map.Entry<Product, Integer> e : order.getItems().entrySet()) {
            Product p = e.getKey();
            int qty = e.getValue();
            double sum = p.getPrice() * qty;
            total += sum;

            sb.append(String.format("%-20s x %-3d = %10.2f%n",
                    p.getName(), qty, sum));
        }

        sb.append(String.format("ИТОГО: %18.2f%n", total));

        return sb.toString();
    }

    public Map<String, Product> getInventory() {
        return inventory;
    }
    public Order placeOrder(Customer customer, Map<Product, Integer> cart) {
        if (blockedCustomers.contains(customer)) {
            System.out.println("Покупатель " + customer.getName() + " заблокирован.");
            return null;
        }

        if (cart == null || cart.isEmpty()) {
            System.out.println("Корзина пуста.");
            return null;
        }
        for (Map.Entry<Product, Integer> e : cart.entrySet()) {
            Product inStock = inventory.get(e.getKey().getCode());

            if (e.getValue() == null || e.getValue() <= 0) {
                System.out.println("Некорректное количество для " + e.getKey().getName());
                return null;
            }
            if (inStock == null) {
                System.out.println("Товар отсутствует на складе: " + e.getKey().getName());
                return null;
            }
            if (inStock.getQuantity() < e.getValue()) {
                System.out.println("Недостаточно " + e.getKey().getName()
                        + ": есть " + inStock.getQuantity()
                        + ", нужно " + e.getValue());
                return null;
            }
        }

        totalOrders++;
        String orderId = "ORD-" + totalOrders;
        Order order = new Order(orderId, customer, cart, Order.Status.NEW);

        orderQueue.offer(order);

        history.computeIfAbsent(customer.getEmail(), k -> new ArrayList<>()).add(order);

        System.out.println("Заказ " + orderId + " оформлен. В очереди: " + orderQueue.size());
        return order;
    }
    public List<Customer> getBlockedCustomersSorted() {
        List<Customer> list = new ArrayList<>(blockedCustomers);
        list.sort(Comparator.comparing(Customer::getName));
        return list;
    }
}
