package vn.gmtrade.model;

public class TradeItem {
    private final char symbol;
    private final String resultType;
    private final String resultId;
    private final int resultAmount;
    private final TradeCost cost;

    public TradeItem(char symbol, String resultType, String resultId, int resultAmount, TradeCost cost) {
        this.symbol = symbol;
        this.resultType = resultType;
        this.resultId = resultId;
        this.resultAmount = resultAmount;
        this.cost = cost;
    }

    public char getSymbol() { return symbol; }
    public String getResultType() { return resultType; }
    public String getResultId() { return resultId; }
    public int getResultAmount() { return resultAmount; }
    public TradeCost getCost() { return cost; }
}
