## 写给自己的--Java反射总结

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

这个对象头中包含的数据很多，包括哈希吗，GC分代年龄，锁状态标志，线程持有的锁等，这些这里不展开说了，大家想了解可以自行查阅相关资料，除了这些还有一个很重要的指针-**类型指针（指向类元数据的指针），刚好解释了我上面的疑问，虚拟机通过这个指针来获取该对象的真实类型**。

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
拿到Class对象之后，我们可以操作以下几类对象 类实现的接口、类的内部类、类的构造器、类的成员变量、类的方法，Class都提供了对应的方法来获取它们。

* getInterfaces()           -- 返回当前类实现的所有接口（不包括从父类继承来的）
* getClasses()              -- 返回当前类和从父类继承来的public内部类
* getDeclaredClasses()      -- 返回当前类的所有内部类(包括private类型，但是不包括从父类继承来的)
* getConstructors()         -- 返回当前类所有的public构造器
* getDeclaredConstructors() -- 返回当前类所有的构造器（包括private类型）
* getConstructor(Class<?>... parameterTypes) -- 根据参数，返回最匹配的构造器对象
* getMethods()              -- 返回当前类和从父类继承来的所有public方法
* getDeclaredMethods()      -- 返回当前类所有的Method方法（包括private类型）
* getDeclaredMethod(String name, Class<?>... parameterTypes) -- 根据参数，返回最匹配的方法
* getFields()               -- 返回当前类和从父类继承来的public字段
* getDeclaredFields()       -- 返回当前类定义的所有字段（包括private）
* getDeclaredField(String name) --返回当前类定义的字段通过参数

上面这些方法不需要死记硬背，需要用到的时候，查看一下文档或者方法说明即可，下面举2个栗子来加深理解。
#### 第一个栗子
需求：现在有个Apple类，它继承于Fruit类，Fruit有一个私有方法seal 参数是一个float类型, 现在要求我们通过反射来调用该私有方法

```
class Apple extends Fruit{}
class Furit {
    public Fruit(int price) {this.price = price;}
    private void seal(int price) {
        L.d("This fruit is sealed by pruce : ¥" + price);
    }
}

private void invokeFruitSeal() {
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

// 查看控制台打印的结果，调用成功
This fruit is sealed by pruce : ¥998.8
```

#### 再举一个栗子
需求：这次我们在Fruit类中新建一个Size类，然后新增一个final的Size常量，我们需要利用反射对其进行修改（正常final常量初始化之后是不能够被修改的，但是利用反射能够做到-。-）

```
class Fruit {
    private final Size size = new Size(50);

    @Override
    public String toString() {
        return "fruit size is : " + size;
    }
}

private void modifyFruitSize() {
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
        //修改之前打印fruit size
        L.d("before modify fruit size" + fruit);
        //获取size字段
        Field sizeField = fruitClazz.getDeclaredField("size");
        //设置私有可访问
        sizeField.setAccessible(true);
        //我们利用反射将size改为非final类型
        Field modifierField = sizeField.getClass().getDeclaredField("modifiers");
        modifierField.setAccessible(true);
        //修改类型
        int modifiers = sizeField.getModifiers() & ~Modifier.FINAL;
        modifierField.set(sizeField, modifiers);
        Size size = new Size(998);
        //设置新的size
        sizeField.set(fruit, size);
        L.d("after modify fruit size" + fruit);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
// 查看控制台打印的结果，调用成功
before modify fruit sizefruit size is : radius is 50
after modify fruit sizefruit size is : radius is 998
```

上面我们通过2个小栗子，熟悉了Java反射的基本操作，除了上面第二个栗子中修改size的final属性稍微难理解一点，还是比较简单的，不过凡事都有一个熟悉的过程，掌握了这些基础的用法，遇到新的需求，能不能使用发射去完成，我们心中就有数了。
如果你觉得这2个小栗子还不过瘾的话，我再看来一个更实际的案例Android Activity 启动 Hook。

### Android Activity 启动 Hook
当然关于Android Activity 启动 Hook的技术网上很多，也有很多很优秀的开源项目，我在这里谈这个有点 关公面前耍大刀的感觉了，但是我在这里谈这个只是为了说明 利用反射 我们可以做很多事情，
所以我就找了这么一个切入点来展示反射能做什么事情，权当抛砖引玉。
我们的需求是，在启动activity时，打印一个Log日志，废话不多说，我们开始。
我们这里只**Hook Activity.startActivity 这种方式的启动**, 看下android framework的相关源码

```
public void startActivity(Intent intent) {
    //常用的activity.startActivity方法
    this.startActivity(intent, null);
}
//...

@Override
public void startActivity(Intent intent, @Nullable Bundle options) {
     if (options != null) {
        startActivityForResult(intent, -1, options);
     } else {
         // Note we want to go through this call for compatibility with
         // applications that may have overridden the method.
         startActivityForResult(intent, -1);
     }
}

public void startActivityForResult(@RequiresPermission Intent intent, int requestCode,
        @Nullable Bundle options) {
     if (mParent == null) {
         options = transferSpringboardActivityOptions(options);
         Instrumentation.ActivityResult ar =
            mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
         //...
     }
     //...
}

private Instrumentation mInstrumentation;

```

我们看到activity.startActivity方法，最终去执行启动操作会用到mInstrumentation这个私有成员变量，所以自然想到它是一个很好的Hook点，分下面三步来走

* 第一步，先获取到该Activity的mInstrumentation
* 第二步，新建一个新的Instrumentation类，重写execStartActivity方法，在执行父类的方法之前加入我们需要的Log日志
* 第三步，将我们新建的新的Instrumentation对象，设置给activity

#### 第一步

```
public static void hook(Activity activity) {
    try {
        Field instrumentationField = Activity.class.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        //...
    }
}
```

#### 第二步

```
public class HookInstrumention extends Instrumentation{

    private Instrumentation mTarget;

    public HookInstrumention(Instrumentation target) {
        this.mTarget = target;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                  Intent intent, int requestCode, Bundle options) {
        L.d("before start activity!");
        Class superClass = Instrumentation.class;
        try {
            Method method = superClass.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            method.setAccessible(true);
            return (ActivityResult) method.invoke(this.mTarget, who, contextThread, token, target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

#### 第三步

```
public static void hook(Activity activity) {
    try {
        Field instrumentationField = Activity.class.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        Instrumentation instrumentation = (Instrumentation) instrumentationField.get(activity);
        HookInstrumention hookInstrumention = new HookInstrumention(instrumentation);
        instrumentationField.set(activity, hookInstrumention);
    } catch (NoSuchFieldException e) {
         e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }
}
```

```
//测试一下:
HookUtil.hook(this);
startActivity(new Intent(MainActivity.this, SecondActivity.class));

//查看log
08-27 14:43:33.391 10298-10298/com.aliouswang.practice.olympic E/hook: before start activity!
08-27 14:43:33.392 10298-10298/com.aliouswang.practice.olympic I/Timeline: Timeline: Activity_launch_request time:427958941 intent:Intent { cmp=com.aliouswang.practice.olympic/.SecondActivity }
```

可以看到我们在Activity 启动之前，成功的打印了一条日志！！

### 写在最后
凡事有失必有得，我们通过反射获得了灵活性，同时也要付出代价，我们会失去编译器优化我们代码的机会，这样我们的代码执行效率会低一些，但是随着JDK版本的不断升级，性能差距会不会缩小，
为了能比较直观的说明这个问题，我决定做一个 小小的实验，来直观的对比一下，反射代码与原生代码的执行效率.

```
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
```

我的电脑配置是mac0S 14，CPU i7，内存16g， 在new 1,000,000 个String对象并调用其toUpperCase方法的情况下，最终测试的结果如下

```
native code use time : 122ms
reflect code use time : 795ms
```
可以看到使用反射的代码比正常编写的代码慢了6倍，虽然差距还是有的，但是这相差的600多ms的时间差平摊到每一次的循环中，那这点差距就显得微不足道了，
所以对性能不是特别敏感的场景，使用反射性能是OK的，当然编程不是为了炫技，我们需要择优选择最好的实现方式。

以上！！各位看官，如果我的总结对你有一点点帮助，点个赞呗，小弟在此先行谢过啦！！！-。-





