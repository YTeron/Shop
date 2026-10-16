package org.example.server.system;

import org.example.server.exception.CustomerBlockedException;
import org.example.server.exception.InvalidQuantityException;
import org.example.server.exception.OutOfStockException;
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

    public Store() {
        this.inventory = new HashMap<>();
        this.orderQueue = new LinkedList<>();
        this.history = new HashMap<>();
        this.blockedCustomers = new HashSet<>();
        load();
        loadOrders();
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
    public void loadOrders() {
        orderQueue.clear();
        history.clear();
        List<Order> loadedOrders = DataBase.jsonReaderForOrder();

        for (Order order : loadedOrders) {
            if (order.getCustomer() != null) {
                history.computeIfAbsent(order.getCustomer().getEmail(), k -> new ArrayList<>()).add(order);
            }
            if ("NEW".equals(order.getStatus())) {
                orderQueue.add(order);
            }
            if (order.getOrderId() != null && order.getOrderId().startsWith("ORD-")) {
                try {
                    int idNum = Integer.parseInt(order.getOrderId().replace("ORD-", ""));
                    if (idNum > totalOrders) {
                        totalOrders = idNum;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }
    public void saveOrders() {
        List<Order> allOrders = new ArrayList<>();
        for (ArrayList<Order> customerOrders : history.values()) {
            allOrders.addAll(customerOrders);
        }
        DataBase.jsonWriterForOrder(allOrders);
    }

    public void addProduct(Product p) {
        inventory.put(p.getCode(), p);
        save();
    }

    public void removeProduct(String code) {
        inventory.remove(code);
        DataBase.jsonRemove(code);
    }
    public Order placeOrder(Customer customer, Map<Product, Integer> cart) throws CustomerBlockedException, OutOfStockException, InvalidQuantityException {
        if (blockedCustomers.contains(customer)) {
            throw new CustomerBlockedException("Покупатель " + customer.getName() + " заблокирован.");
        }

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int requestedQty = entry.getValue();

            if (requestedQty <= 0) {
                throw new InvalidQuantityException("Некорректное количество для товара: " + product.getName());
            }

            Product stockProduct = inventory.get(product.getCode());
            if (stockProduct == null || stockProduct.getQuantity() < requestedQty) {
                throw new OutOfStockException("Недостаточно товара на складе: " + product.getName());
            }
        }

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int requestedQty = entry.getValue();
            Product stockProduct = inventory.get(product.getCode());
            stockProduct.setQuantity(stockProduct.getQuantity() - requestedQty);
        }
        save();

        totalOrders++;
        String orderId = "ORD-" + totalOrders;
        Order order = new Order(orderId, customer, cart, Order.Status.NEW);

        orderQueue.add(order);
        history.computeIfAbsent(customer.getEmail(), k -> new ArrayList<>()).add(order);
        saveOrders();
        return order;
    }
    public void processNextOrder() {
        Order order = orderQueue.poll();
        if (order != null) {
            order.setStatus("PROCESSED");
            System.out.println("Заказ " + order.getOrderId() + " успешно обработан.");
            saveOrders();
        } else {
            System.out.println("Очередь заказов пуста.");
        }
    }


    public List<Product> searchByName(String query) {
        List<Product> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Product p : inventory.values()) {
            if (p.getName().toLowerCase().contains(lowerQuery)) {
                result.add(p);
            }
        }
        return result;
    }

    public String buildReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID заказа: ").append(order.getOrderId()).append("\n");
        sb.append("Статус: ").append(order.getStatus()).append("\n");
        sb.append("Покупатель: ").append(order.getCustomer().getName())
                .append(" (").append(order.getCustomer().getEmail()).append(")\n");
        sb.append(String.format("%-20s %-10s %-10s\n", "Товар", "Кол-во", "Цена"));

        double totalSum = 0;
        for (Map.Entry<Product, Integer> entry : order.getItems().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double sum = p.getPrice() * qty;
            totalSum += sum;
            sb.append(String.format("%-20s %-10d %-10.2f\n", p.getName(), qty, sum));
        }
        sb.append(String.format("Итого к оплате: %.2f\n", totalSum));
        return sb.toString();
    }

    public TreeMap<String, Integer> salesByCategory() {
        TreeMap<String, Integer> salesMap = new TreeMap<>();
        for (ArrayList<Order> orders : history.values()) {
            for (Order order : orders) {
                if ("PROCESSED".equals(order.getStatus())) {
                    for (Map.Entry<Product, Integer> entry : order.getItems().entrySet()) {
                        String category = entry.getKey().getClass().getSimpleName();
                        salesMap.put(category, salesMap.getOrDefault(category, 0) + entry.getValue());
                    }
                }
            }
        }
        return salesMap;
    }
    public List<Customer> getBlockedCustomersSorted() {
        List<Customer> sortedList = new ArrayList<>(blockedCustomers);
        sortedList.sort(Comparator.comparing(Customer::getName));
        return sortedList;
    }

    public Map<String, Product> getInventory() {
        return inventory;
    }
}
