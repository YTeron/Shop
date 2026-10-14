package org.example;

import org.example.server.exception.StoreException;
import org.example.server.people.Customer;
import org.example.server.system.DataBase;

import org.example.server.system.Product;
import org.example.server.system.Store;
import org.example.server.test.Clothing;
import org.example.server.test.Electronics;
import org.example.server.test.Food;

import java.util.*;

public class App 
{
    private static final Store store = new Store();
    private static final Scanner sc = new Scanner(System.in);
    public static void main( String[] args )
    {

        int choice;
        do {
            printMenu();
            choice = readInt();
            try {
                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> removeProduct();
                    case 3 -> searchByName();
                    case 4 -> placeOrder();
                    case 5 -> store.processNextOrder();
                    case 6 -> printSales();
                    case 7 -> printBlocked();
                    case 8 -> printInventory();
                    case 0 -> System.out.println("Выход.");
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private static void printMenu() {
        System.out.println("""

                1. Добавить товар
                2. Удалить товар
                3. Поиск по названию
                4. Оформить заказ
                5. Обработать следующий заказ
                6. Продажи по категориям
                7. Заблокированные покупатели
                8. Показать склад
                0. Выход
                """);
        System.out.print("Выбор: ");
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }

    private static void addProduct() {
        System.out.print("Категория (1-Одежда, 2-Электроника, 3-Еда): ");
        int cat = readInt();
        System.out.print("Артикул: ");
        String code = sc.nextLine().trim();
        System.out.print("Название: ");
        String name = sc.nextLine().trim();
        System.out.print("Цена: ");
        double price = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Количество: ");
        int qty = readInt();

        Product p = switch (cat) {
            case 1 -> {
                System.out.print("Размер: ");
                yield new Clothing(code, name, price, qty, sc.nextLine().trim());
            }
            case 2 -> {
                System.out.print("Гарантия (мес): ");
                yield new Electronics(code, name, price, qty, readInt());
            }
            case 3 -> {
                System.out.print("Срок годности (дн): ");
                yield new Food(code, name, price, qty, readInt());
            }
            default -> throw new IllegalArgumentException("Неверная категория");
        };

        store.addProduct(p);
        store.save();
    }

    private static void removeProduct() {
        System.out.print("Артикул: ");
        String code = sc.nextLine().trim();
        store.removeProduct(code);
        store.save();
    }

    private static void searchByName() {
        System.out.print("Запрос: ");
        List<Product> found = store.searchByName(sc.nextLine().trim());
        if (found.isEmpty()) System.out.println("Ничего не найдено.");
        else found.forEach(System.out::println);
    }

    private static void placeOrder() {
        System.out.print("Имя покупателя: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        Customer c = new Customer(name, email);

        Map<Product, Integer> cart = new HashMap<>();
        while (true) {
            System.out.print("Артикул (Enter — закончить): ");
            String code = sc.nextLine().trim();
            if (code.isEmpty()) break;

            Product p = store.getInventory().get(code);
            if (p == null) {
                System.out.println("Нет такого товара.");
                continue;
            }
            System.out.print("Количество: ");
            int q = readInt();
            cart.put(p, q);
        }
        store.placeOrder(c, cart);
    }

    private static void printSales() {
        TreeMap<String, Integer> sales = store.salesByCategory();
        if (sales.isEmpty()) System.out.println("Продаж нет.");
        else sales.forEach((k, v) -> System.out.printf("  %-15s %d шт.%n", k, v));
    }

    private static void printBlocked() {
        if (store.getBlockedCustomersSorted().isEmpty()) System.out.println("Заблокированных нет.");
        else store.getBlockedCustomersSorted().forEach(System.out::println);
    }
    private static void printInventory() {
        if (store.getInventory().isEmpty()) System.out.println("Склад пуст.");
        else store.getInventory().values().forEach(System.out::println);
    }
}
