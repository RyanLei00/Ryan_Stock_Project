public class Stock {
    private String name;
    private double opening;
    private double closing;
    private double highest;
    private double lowest;

    public Stock(String name, double opening, double closing, double highest, double lowest) {
        this.name = name;
        this.opening = opening;
        this.closing = closing;
        this.highest = highest;
        this.lowest = lowest;
    }

    public String getName() {
        return name;
    }

    public double getOpening() {
        return opening;
    }

    public double getClosing() {
        return closing;
    }

    public double getHighest() {
        return highest;
    }

    public String toString() {
        String str = "Stock Name: " + name + ", Opening Price: " + opening + ", Closing Price: " + closing;
        str += ", Highest Price: " + highest + ", Lowest Price: " + lowest;
        return str;
    }
}