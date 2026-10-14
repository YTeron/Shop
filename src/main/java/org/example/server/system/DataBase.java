package org.example.server.system;

import com.github.cliftonlabs.json_simple.*;
import org.example.server.test.Clothing;
import org.example.server.test.Electronics;
import org.example.server.test.Food;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


import static javax.swing.UIManager.getInt;
import static javax.swing.UIManager.getString;

public class DataBase {
    public static void jsonWriter(List<Product> items){
        Map<String, JsonArray> categories = new HashMap<>();
        int count = 0;
        for (Product p: items){
            if (p != null) {
                count++;
                System.out.println("Найден: " + p.getClass().getName());
                String className = p.getClass().getSimpleName();
                JsonObject itemJson = p.toJson();
                categories
                        .computeIfAbsent(className, k -> new JsonArray())
                        .add(itemJson);
            }
        }
        JsonObject rootJson = new JsonObject();
        for (Map.Entry<String, JsonArray> entry : categories.entrySet()) {
            rootJson.put(entry.getKey(), entry.getValue());
        }
        System.out.println("Всего найдено реализаций: " + count);
        jsonInit(rootJson);
    }
    public static List<Product> jsonReader(){
        List<Product> products =new ArrayList<>();
        File file = new File("products.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)){
                JsonObject rootJson = (JsonObject) Jsoner.deserialize(reader);
                for (String categoryKey : rootJson.keySet()) {
                    Object value = rootJson.get(categoryKey);
                    if (value instanceof JsonArray jsonArray) {
                        for (Object itemObj : jsonArray) {
                            if (itemObj instanceof JsonObject itemJson) {
                                String code = getString(itemJson.get("code"));
                                String name = getString(itemJson.get("name"));
                                double price = getDouble(itemJson.get("price"));
                                int quantity = getInt(itemJson.get("quantity"));
                                Product product = createProductByCategory(categoryKey, code, name, price, quantity,itemJson);
                                products.add(product);
                            }
                        }
                    }
                }

            }
            catch (IOException | JsonException e){
            }
        } else {
            System.err.println("Файл не найден");
        }
        return products;
    }
    private static Product createProductByCategory(String categoryKey, String code, String name, double price, int quantity, Object itemObj) {
        return switch (categoryKey) {
            case "Clothing" -> new Clothing(code, name, price, quantity, getString(((JsonObject)itemObj).get("size")));
            case "Electronics" -> new Electronics(code, name, price, quantity, getInt(((JsonObject)itemObj).get("warrantyMonths")));
            case "Food" -> new Food(code, name, price, quantity, getInt(((JsonObject)itemObj).get("shelfLifeDays")));
            default -> throw new IllegalStateException("Unexpected value: " + categoryKey);
        };
    }

    public static void jsonRemove(String code){
        List<Product> all = jsonReader();
        boolean b = all.removeIf(product -> product.getCode().equals(code));

        if (b) {
            System.out.println("Товар с кодом удалён " + code);
            jsonWriter(all);
        }
        else System.err.println("Товар с таким кодом не найден " + code);

    }
    public static void jsonAdd(Product p){
        List<Product> all = jsonReader();
        for (Product existing : all) {
            if (existing.getCode().equals(p.getCode())) {
                System.err.println("Товар с кодом " + p.getCode() + " уже есть.");
                return;
            }
        }
        all.add(p);
        jsonWriter(all);
        System.out.println("Товар добавлен: " + p.getName());
    }
    private static void jsonInit(JsonObject items){
        File file = new File("products.json");
        try {
            if (!file.exists() && file.createNewFile()) {
                System.out.println("Файл products.json создан.");
            }
            String rawJson = Jsoner.serialize(items);
            String prettyJson = Jsoner.prettyPrint(rawJson);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(prettyJson);
                System.out.println("Данные успешно записаны в файл!");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static String getString(Object val) {
        return val != null ? val.toString() : "";
    }

    private static double getDouble(Object val) {
        if (val instanceof BigDecimal b) return b.doubleValue();
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private static int getInt(Object val) {
        if (val instanceof BigDecimal b) return b.intValue();
        if (val instanceof Number n) return n.intValue();
        return 0;
    }
}
