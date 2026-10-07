package vn.gmtrade.model;

public class RequiredItem {
    private final String type;
    private final String id;
    private final int amount;

    public RequiredItem(String type, String id, int amount) {
        this.type = type;
        this.id = id;
        this.amount = amount;
    }

    public String getType() { return type; }
    public String getId() { return id; }
    public int getAmount() { return amount; }
}
