package com.ait.inventory;

public class Product {
    private int id;
    private String name;
    private String sku;
    private String category;
    private int quantity;
    private double price;  // unit price

    public Product() {}

    public Product(int id, String name, String sku, String category, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public Product(String name, String sku, String category, int quantity, double price) {
        this(0, name, sku, category, quantity, price);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /** Unit price */
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    /** Convenience: total value of this product's stock = quantity × unit price */
    public double getTotalValue() {
        return price * quantity;
    }
}
