package vn.gmtrade.model;

import java.util.List;

public class TradeCost {
    private final double money;
    private final List<RequiredItem> items;

    public TradeCost(double money, List<RequiredItem> items) {
        this.money = money;
        this.items = items;
    }

    public double getMoney() { return money; }
    public List<RequiredItem> getItems() { return items; }
}
