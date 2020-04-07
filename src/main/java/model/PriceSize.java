package model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PriceSize {
    private int price;
    private int size;
    private char type;


    public PriceSize(int price, int size, char type) {
        this.price = price;
        this.size = size;
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceSize)) return false;
        PriceSize priceSize = (PriceSize) o;
        return getPrice() == priceSize.getPrice() &&
                getSize() == priceSize.getSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrice(), getSize());
    }

}
