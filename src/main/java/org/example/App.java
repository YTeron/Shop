package org.example;

import org.example.server.exception.CustomerBlockedException;
import org.example.server.exception.InvalidQuantityException;
import org.example.server.exception.OutOfStockException;
import org.example.server.exception.StoreException;
import org.example.server.people.Customer;
import org.example.server.people.Order;
import org.example.server.system.DataBase;

import org.example.server.system.Product;
import org.example.server.system.Store;
import org.example.server.test.Clothing;
import org.example.server.test.Electronics;
import org.example.server.test.Food;

import java.util.*;

public class App 
{
    public static void main( String[] args )
    {
        Store store = new Store();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Выберите пункт меню: ");

            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                System.out.println();
                switch (choice) {
                    case 1:
                        showInventory(store);
                        break;
                    case 2:
                        addProductMenu(store, scanner);
                        break;
                    case 3:
                        removeProductMenu(store, scanner);
                        break;
                    case 4:
                        searchProductMenu(store, scanner);
                        break;
                    case 5:
                        placeOrderMenu(store, scanner);
                        break;
                    case 6:
                        store.processNextOrder();
                        break;
                    case 7:
                        showSalesCategoryReport(store);
                        break;
                    case 8:
                        showBlockedCustomers(store);
                        break;
                    case 0:
                        running = false;
                        System.out.println("Выход из программы. До свидания!");
                        break;
                    default:
                        System.err.println("Неверный пункт меню. Введите число от 0 до 8.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка ввода: пожалуйста, введите целое число.");
            } catch (StoreException e) {
                System.err.println("Ошибка бизнес-логики: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("---------------- МЕНЮ ----------------");
        System.out.println("1. Показать список товаров на складе");
        System.out.println("2. Добавить новый товар");
        System.out.println("3. Удалить товар по артикулу (коду)");
        System.out.println("4. Поиск товара по названию");
        System.out.println("5. Оформить заказ");
        System.out.println("6. Обработать следующий заказ (FIFO)");
        System.out.println("7. Отчёт: продажи по категориям");
        System.out.println("8. Список заблокированных покупателей");
        System.out.println("0. Выход");
        System.out.println("--------------------------------------");
    }

    private static void showInventory(Store store) {
        Map<String, Product> inventory = store.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Склад пуст.");
            return;
        }
        System.out.println("ТОВАРЫ НА СКЛАДЕ");
        for (Product p : inventory.values()) {
            System.out.println("Код: " + p.getCode() + " | Название: " + p.getName() +
                    " | Цена: " + p.getPrice() + " | Остаток: " + p.getQuantity());
        }
    }

    private static void addProductMenu(Store store, Scanner scanner) {
        System.out.println("Выберите категорию товара:");
        System.out.println("1. Электроника (Electronics)");
        System.out.println("2. Одежда (Clothing)");
        System.out.println("3. Продукты (Food)");
        System.out.print("Ваш выбор: ");

        String catChoice = scanner.nextLine();

        System.out.print("Введите артикул (код): ");
        String code = scanner.nextLine();
        System.out.print("Введите название: ");
        String name = scanner.nextLine();
        System.out.print("Введите цену: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Введите количество: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        Product product = null;
        switch (catChoice) {
            case "1":
                System.out.print("Введите гарантию (месяцев): ");
                int warranty = Integer.parseInt(scanner.nextLine());
                product = new Electronics(code, name, price, quantity, warranty);
                break;
            case "2":
                System.out.print("Введите размер (например, L, M, 42): ");
                String size = scanner.nextLine();
                product = new Clothing(code, name, price, quantity, size);
                break;
            case "3":
                System.out.print("Введите срок годности (дней): ");
                int shelfLife = Integer.parseInt(scanner.nextLine());
                product = new Food(code, name, price, quantity, shelfLife);
                break;
            default:
                System.err.println("Неверная категория.");
                return;
        }

        store.addProduct(product);
        System.out.println("Товар успешно добавлен!");
    }

    private static void removeProductMenu(Store store, Scanner scanner) {
        System.out.print("Введите артикул товара для удаления: ");
        String code = scanner.nextLine();
        store.removeProduct(code);
    }

    private static void searchProductMenu(Store store, Scanner scanner) {
        System.out.print("Введите поисковый запрос (название): ");
        String query = scanner.nextLine();
        List<Product> found = store.searchByName(query);
        if (found.isEmpty()) {
            System.out.println("Ничего не найдено.");
        } else {
            System.out.println("Найдено товаров: " + found.size());
            for (Product p : found) {
                System.out.println("- " + p.getName() + " (Цена: " + p.getPrice() + ", Остаток: " + p.getQuantity() + ")");
            }
        }
    }

    private static void placeOrderMenu(Store store, Scanner scanner) throws StoreException {
        System.out.print("Введите имя покупателя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email покупателя: ");
        String email = scanner.nextLine();
        Customer customer = new Customer(name, email);

        Map<Product, Integer> cart = new HashMap<>();
        boolean adding = true;

        while (adding) {
            System.out.print("Введите артикул товара для добавления в корзину (или 'stop' для завершения): ");
            String code = scanner.nextLine();
            if (code.equalsIgnoreCase("stop")) {
                break;
            }

            Product product = store.getInventory().get(code);
            if (product == null) {
                System.err.println("Товар с таким артикулом не найден на складе.");
                continue;
            }

            System.out.print("Введите количество: ");
            int qty = Integer.parseInt(scanner.nextLine());
            cart.put(product, qty);
        }

        if (cart.isEmpty()) {
            System.out.println("Корзина пуста, заказ не оформлен.");
            return;
        }

        Order order = store.placeOrder(customer, cart);
        System.out.println("\nЗаказ успешно оформлен!");
        System.out.println(store.buildReceipt(order));
    }

    private static void showSalesCategoryReport(Store store) {
        TreeMap<String, Integer> sales = store.salesByCategory();
        if (sales.isEmpty()) {
            System.out.println("Нет данных о проданных товарах.");
            return;
        }
        System.out.println("=== ПРОДАЖИ ПО КАТЕГОРИЯМ ===");
        for (Map.Entry<String, Integer> entry : sales.entrySet()) {
            System.out.println("Категория: " + entry.getKey() + " | Продано штук: " + entry.getValue());
        }
    }

    private static void showBlockedCustomers(Store store) {
        List<Customer> blocked = store.getBlockedCustomersSorted();
        if (blocked.isEmpty()) {
            System.out.println("Заблокированных покупателей нет.");
            return;
        }
        System.out.println("=== ЗАБЛОКИРОВАННЫЕ ПОКУПАТЕЛИ ===");
        for (Customer c : blocked) {
            System.out.println("Имя: " + c.getName() + " | Email: " + c.getEmail());
        }
    }
}
