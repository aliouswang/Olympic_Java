package com.aliouswang.practice.olympic.reflect;

import com.aliouswang.practice.olympic.bean.TestClassLoader;
import com.aliouswang.practice.olympic.util.L;

public class TestClass {

    public static void main(String[] args) {

        Class<?> clazz = TestClassLoader.class;
        try {
            clazz = Class.forName("com.aliouswang.practice.olympic.bean.TestClassLoader");
            L.d("before new instance");
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
