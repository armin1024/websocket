package com.armin.thread.particularity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发编程三大特性其一：原子性
 */
public class ThreadAtomicity {

    public static void main(String[] args) throws InterruptedException {
        testMuchThreadIncrement();
    }

    private static int count;

    public static void increment() {
        //使用同步代码块或同步方法，保证多线程的操作同一变量的原子性；
        //底层原理就是将临界资源加锁，始终只有一个线程在同一CPU时刻操作该临界资源
        synchronized (ThreadAtomicity.class) {
            count++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 多线程对共享变量的操作会出现与结果不一致的情况，此种情况未保证证原子性
     * 在线程1拿到count变量的值为1的时候线程2也拿到count的值为1他们同时对count做++操作然后在赋值给count，此时count的结果实际上就做了一次++
     *
     * @throws InterruptedException e
     */
    public static void testMuchThreadIncrement() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                increment();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                increment();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(count); //理论输出为200，结果每次都会小于200
    }

}

/**
 * Compare and Swap比较和交换，它是一条CPU的并发原语（synchronized对程序的性能较CAS逊色）
 * 它在替换内存的某个位置时，首先会查看内存中的值与预期值是否一致，如果一致，执行替换操作。这个操作是一个原子性操作
 *
 * Java中基于Unsafe的类提供了对CAS的操作的方法，JVM会帮助我们将方法实现CAS汇编指令
 * 但是要清楚CAS只是比较和交换，在获取原值的这个操作上，需要你自己实现
 *
 * 缺点：
 *  - CAS只能保证对一个变量的操作是原子性的，无法实现对多行代码实现原子性
 * CAS的问题：
 *  - ABA问题：三个线程同时去更新一个变量，由于CPU的调度问题，执行顺序无法保证，但是CAS可以让CPU一个一个进行对比交换
 *      首先：第一个线程 -> 比较变量值A对其进行赋值B
 *      其次：第二个线程 -> 比较变量值B对其进行赋值A
 *      最后：第三个线程 -> 比较变量值A对其进行赋值B
 *  最终可以发现最终的变量结果是没有变化的，如果要知道变量被改变的次数就需要加入版本号来解决该问题
 *  ABA的问题，如果只考虑数据的总量合理，那可以不关注ABA问题。如果业务关心CAS的操作次数，例如一个开关，
 *  每次操作是从true改为false，也有从false改为true，如果没有ABA问题，可能连续的开关了很多次，业务还能跑，
 *  而你看恰巧要关注这种开关的次数，此时ABA问题就需要考虑进去
 *  Java中提供了一个类在CAS时，针对各个版本追加版本号的操作 -> AtomicStampedReference（在CAS时，不但会判断原值，还会比较版本信息）
 *
 *  - 自旋时间过长问题
 *      1.可以指定CAS一共循环多少此，如果超过这个次数，直接失败/或挂起线程（自旋锁，自适应自旋锁）
 *      2.可以在CAS一次失败后，将这个操作暂存起来，后面需要获取结果时，将暂存的操作全部执行，再返回最后的结果（如：LongAdder）
 */
class CAS {

    public static void main(String[] args) throws InterruptedException {
        testMuchThreadIncrement();

        //通过AtomicStampedReference解决ABA问题
        AtomicStampedReference<String> reference = new AtomicStampedReference<>("A", 1);
        String oldVal = reference.getReference();
        int oldVersion = reference.getStamp();
        boolean b = reference.compareAndSet(oldVal, "B", oldVersion, oldVersion + 1);
        System.out.println("第一次修改:" + b); //true

        //由于第二次修改时他的老版本应该是oldVersion + 1 = 2而不是1所以更新会失败
        boolean c = reference.compareAndSet("B", "C", 1, 1 + 1);
        System.out.println("第二次修改:" + c); //false

        System.out.println("value:"+reference.getReference()+", version:"+reference.getStamp()); //value:B, version:2
    }

    static AtomicInteger count = new AtomicInteger(0);

    public static void testMuchThreadIncrement() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                count.getAndIncrement();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                count.getAndIncrement();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(count); //使用AtomicInteger中基于Unsafe的getAndIncrement()方法通过CAS保证了共享变量的原子性
    }

}

/**
 * Lock锁是再JDK1.5由Doug Lea研发的，他的性能相比synchronized在JDK1.5的时期，性能好了很多，但是在JDK.16对
 * synchronized优化之后，性能相差不大，但是如果涉及并发比较多时，推荐ReentrantLock锁，性能会更好
 *
 * ReentrantLock可以直接对比synchronized，在功能上来说，都时锁，但是ReentrantLock的功能性相比synchronized更丰富
 *
 * ReentrantLock底层是基于AQS实现的，有一个基于CAS维护的state变量来实现锁的操作
 */
class Lock {

    public static void main(String[] args) throws InterruptedException {
        testMuchThreadIncrement();
    }

    static ReentrantLock lock = new ReentrantLock();

    static int count;

    public static void increment() {
        try {
            lock.lock();
            count++;
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //使用finally解锁不论业务中出现什么错误，防止锁未释放情况
            lock.unlock();
        }
    }

    public static void testMuchThreadIncrement() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                increment();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                increment();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(count); //理论输出为200，结果每次都会小于200
    }

}

/**
 * ThreadLocal保证原子性的方式，是不让多线程去操作临界资源，让每个线程去操作属于自己的数据
 * 实现原理：
 *  1.每个Thread中都存储着一个成员变量，ThreadLocalMap
 *  2.ThreadLocal本身不存储数据，像是一个工具类，基于ThreadLocal去操作ThreadLocalMap
 *  3.ThreadLocalMap本身就是基于Entry[]实现的，因为一个线程可以绑定多个ThreadLocal，这样以来，可能需要存储多个数据，
 *      所以采用Entry[]的形式实现
 *  4.每一个线程都有自己独立的ThreadLocalMap，再基于ThreadLocal对象本身作为key，对value进行存取
 *  5.ThreadLocalMap的key是一个弱引用，弱引用的特点是，即使由弱引用，在GC时，也必须被回收，这里是为了在ThreadLocal
 *      对象失去引用后，如果key的引用是强引用，会导致ThreadLocal对象无法被回收
 *
 * ThreadLocal内存泄露问题：
 *  1.如果ThreadLocal引用丢失，key因为弱引用会被GC回收掉，如果同时线程还没有被回收，就会导致内存泄漏，
 *      内存中的value无法被回收，同时也无法被获取到
 *  2.只需要在使用完毕ThreadLocal对象之后，及时的调用remove()，移除Entry即可
 */
class ThreadLocalExp {

    static ThreadLocal<String> tl1 = new ThreadLocal<>();
    static ThreadLocal<String> tl2 = new ThreadLocal<>();

    public static void main(String[] args) {
        //在主线程中set的值在t1线程中是拿不到的，如果需要在t1线程中获取就需要在t1线程中进行赋值
        tl1.set("123");
        tl2.set("456");
        Thread t1 = new Thread(() -> {
            tl1.set("abc");
            tl2.set("efg");
            System.out.println("t1:" + tl1.get());
            System.out.println("t1:" + tl2.get());
        });
        t1.start();

        System.out.println("main:" + tl1.get());
        System.out.println("main:" + tl2.get());
    }

}
