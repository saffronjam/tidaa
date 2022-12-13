package application;

public class Product {

    private int id;
    private String name;
    private int stock;
    private int price;
    private Category category;

    public Product(int id, String name, int stock, int price, Category category) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public int getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }
}
