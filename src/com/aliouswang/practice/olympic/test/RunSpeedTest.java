package com.aliouswang.practice.olympic.test;

import com.aliouswang.practice.olympic.util.L;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RunSpeedTest {

    public static void main(String[] args) {
        int total = 1000000;
        testNativeCode(total);
        testReflectCode(total);
    }

    private static void testNativeCode(int total) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            String orinal = i + "";
            String str = new String(orinal);
            str.toUpperCase();
        }
        L.d("native code use time : " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static void testReflectCode(int total) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            String orinal = i + "";
            Class<?> strClazz = String.class;
            String str = null;
            try {
                Constructor<?> constructor = strClazz.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                str = (String) constructor.newInstance(orinal);
                Method method = strClazz.getMethod("toUpperCase");
                method.setAccessible(true);
                method.invoke(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        L.d("reflect code use time : " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
