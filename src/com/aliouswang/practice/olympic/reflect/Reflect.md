## 写给自己的--Java反射

> 作为写了几年Java代码的小菜鸡，反射这个知识点一直只停留在知道、了解的水平，最近在看Android插件化方面的知识，有点小吃力，反思之后-v-，最终想明白了，是无知限制了我的想象力，痛定思痛，于是有了这篇Java反射的总结--送给自己。

### 反射的定义
> Reflection enables Java code to discover information about the fields, methods and constructors of loaded classes, and to use reflected fields, methods, and constructors to operate on their underlying counterparts, within security restrictions. The API accommodates applications that need access to either the public members of a target object (based on its runtime class) or the members declared by a given class. It also allows programs to suppress default reflective access control.
>
> 蹩脚大意翻译：反射能够让Java代码获取一个已经加载的类的字段，方法，构造器等信息，并能够访问它们不受访问权限的控制（private也能访问）

上面反射是Oracle官方文档的定义，反射能够突破访问权限控制，这还是很优秀的，但是，问题来了，**为什么需要反射或者说什么情况下需要用反射？**

### 为什么需要反射
我们平时在IDE中写的Java代码，工程中新建的类，只要包含该类的包，就能访问该类的公共方法，然后类的稀有方法被封装隐藏起来，这一切都看起来很合理啊，那为什么还需要反射呢？其实一切都为了**动态性**，譬如以下几种情况

* 一个类是从云端下载的，或者其他文件路径加载进来的，那么怎么使用该类呢，我们需要反射
* 框架为了封装性，肯定会尽量暴露最少的信息给外部类使用，站在技术的角度没毛病，可是需求总是善变的（还记得根据手机壳颜色设置app主题？）所以有时候我需要更大的权限，我们需要反射
* Java的动态代理，代理类是动态生成的，我们需要反射

当然，反射还有很多其他应用，它们的根本目的就是为了语言的**动态性**。

### 初识反射
我们都知道Java世界中一切皆对象，要学习使用反射，首先我们要理解Class对象，以最常见的HotSpot虚拟机为例，被加载的类都会被保存到方法区，比如基础类型 int.class , double.class 等，还有 容器库 ArrayList.class, HashMap.class 等，所有的类的元数据都在方法区，看一个例子

```
ArrayList arrayList = new ArrayList();
```
就一行代码，新建了一个ArrayList对象, 那么arrayList指针会被push进当前调用的方法栈中，我们可以想的更进一步，Java虚拟机是如何知道arrayList的真实类型的？要解释这个，我还要了解Java对象头的一些知识

##### Java对象头
还是以HotSpot为例，对象在内存中的数据分三部分：

* 对象头  （我们关注的重点）
* 实例数据  （很好理解，就是类对象成员变量等数据）
* 字节对齐填充 （虚拟机规范要求对象起始地址必须是8的倍数）

这个对象头中包含的数据很多，包括哈希吗，GC分代年龄，锁状态标志，线程持有的锁等，这些这里不展开说了，除了这些还有一个很重要的指针-**类型指针（指向类元数据的指针），刚好解释了我上面的疑问，虚拟机通过这个指针来获取该对象的真实类型**。

### 反射的常规操作
要使用反射我们就要获得Class对象，获取方法有以下三种

* List.class
* new ArrayList().getClass()
* Class.forName("java.util.ArrayList")

这三种方法都可以获取到对象对应的Class对象，具体使用哪个要看具体情况，比如你只有该类的类名，那么可以选择第三种，再比如你已经有一个该类的类对象，那么可以选择第二种方法。这里要注意一点，**第一种方法只会触发类的加载不会触发类的初始化，第二，三种方法会同时触发类加载和初始化(如果需要的话)。** 关于这个 我们写一个最简单的demo测试一下

```
public class TestClassLoader {
    static {
        L.d("TestClassLoader class init!");
    }
}

...

public static void main(String[] args) {
    Class<?> clazz = TestClassLoader.class;
    L.d("after TestClassLoader.class");
    try {
        clazz = Class.forName("com.aliouswang.practice.olympic.bean.TestClassLoader");
        L.d("after Class.forName");
        TestClassLoader testClassLoader = (TestClassLoader) clazz.newInstance();
        L.d("after new instance");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

//控制台打印的结果
after TestClassLoader.class
TestClassLoader class init! static value is 100
after Class.forName
after new instance
```

可以看到TestClassLoader 类的静态代码块在Class.forName()方法调用之前被初始化，而调用TestClassLoader.class时并没有触发初始化，这个简单的例子也就验证了我们上面的结论。
拿到Class对象之后，我们可以操作以下几类对象。

* Interface,


