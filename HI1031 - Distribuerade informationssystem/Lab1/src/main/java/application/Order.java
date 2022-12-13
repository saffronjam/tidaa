package application;

import java.sql.Timestamp;
import java.sql.Timestamp;
import java.util.HashMap;

public class Order {
    private final int id;
    private final Timestamp ordered;
    private final Timestamp packed;
    private final Timestamp shipped;
    private final int userId;
    private final HashMap<Product, Integer> productIds;

    public Order(int id, Timestamp ordered, Timestamp packed, Timestamp shipped, int userId, HashMap<Product, Integer> productIds) {
        this.id = id;
        this.ordered = ordered;
        this.packed = packed;
        this.shipped = shipped;
        this.userId = userId;
        this.productIds = productIds;
    }

    public int getId() {
        return this.id;
    }

    public Timestamp getOrdered() {
        return ordered;
    }

    public Timestamp getPacked() {
        return packed;
    }

    public Timestamp getShipped() {
        return shipped;
    }

    public int getUserId() {
        return userId;
    }

    public HashMap<Product, Integer> getProductIds() {
        return this.productIds;
    }
}
