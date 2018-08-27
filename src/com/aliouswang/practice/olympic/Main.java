package com.aliouswang.practice.olympic;

import com.aliouswang.practice.olympic.bean.Apple;
import com.aliouswang.practice.olympic.bean.Fruit;
import com.aliouswang.practice.olympic.bean.Size;
import com.aliouswang.practice.olympic.util.L;

import java.lang.reflect.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        modifyFruitSize();

    }

    private static void modifyFruitSize() {
        try {
            //获取Apple类
            Class clazz = Class.forName("com.aliouswang.practice.olympic.bean.Apple");
            //获取Apple的直接父类Fruit
            Class fruitClazz = clazz.getSuperclass();
            //获取父类的私有构造器
            Constructor<?> constructor = fruitClazz.getDeclaredConstructor(int.class);
            //设置私有可访问
            constructor.setAccessible(true);
            //通过私有构造器新建Fruit对象
            Fruit fruit = (Fruit) constructor.newInstance(0);

            L.d("before modify fruit size" + fruit);

            Field sizeField = fruitClazz.getDeclaredField("size");
            sizeField.setAccessible(true);
            Field modifierField = sizeField.getClass().getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            int modifiers = sizeField.getModifiers() & ~Modifier.FINAL;
            modifierField.set(sizeField, modifiers);
            Size size = new Size(998);
            sizeField.set(fruit, size);
            L.d("after modify fruit size" + fruit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeFurit() {
        try {
            //获取Apple类
            Class clazz = Class.forName("com.aliouswang.practice.olympic.bean.Apple");
            //获取Apple的直接父类Fruit
            Class fruitClazz = clazz.getSuperclass();
            //获取父类的私有构造器
            Constructor<?> constructor = fruitClazz.getDeclaredConstructor(int.class);
            //设置私有可访问
            constructor.setAccessible(true);
            //通过私有构造器新建Fruit对象
            Fruit fruit = (Fruit) constructor.newInstance(0);
            //获取fruit的私有方法
            Method sealMethod = fruitClazz.getDeclaredMethod("seal", float.class);
            sealMethod.setAccessible(true);
            //调用方法
            sealMethod.invoke(fruit, 998.8f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
