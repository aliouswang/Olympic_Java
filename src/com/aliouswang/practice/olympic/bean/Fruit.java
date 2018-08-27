package com.aliouswang.practice.olympic.bean;

import com.aliouswang.practice.olympic.interfaces.IFood;
import com.aliouswang.practice.olympic.util.L;

public class Fruit implements IFood {

    private final Size size = new Size(50);

    protected float price;

    public Fruit() {

    }

    private Fruit(int price) {
        this.price = price;
    }

    private class Weight {

    }

    private void seal(float price) {
        L.d("This fruit is sealed by pruce : ¥" + price);
    }

    public void sealPublic() {

    }

    @Override
    public String toString() {
        return "fruit size is : " + size;
    }
}
