package com.aliouswang.practice.olympic.reflect;

import com.aliouswang.practice.olympic.bean.TestClassLoader;
import com.aliouswang.practice.olympic.util.L;

import java.util.ArrayList;
import java.util.List;

public class TestClass {

    public static void main(String[] args) {

        List arrayList = new ArrayList<>();

        Class<?> clazz = TestClassLoader.class;
        L.d("after TestClassLoader.class");
        try {
            clazz = Class.forName("com.aliouswang.practice.olympic.bean.TestClassLoader");
            L.d("after Class.forName");
            TestClassLoader testClassLoader = (TestClassLoader) clazz.newInstance();
            L.d("after new instance");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

}
