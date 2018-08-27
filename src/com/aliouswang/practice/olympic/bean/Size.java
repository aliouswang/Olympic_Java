package com.aliouswang.practice.olympic.bean;

public class Size {

    private int radius;

    public Size(int radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "radius is " + radius;
    }
}
