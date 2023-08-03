package com.armin.thread.visibility;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发编程三大特性其一：可见性
 *
 * 可见性问题是基于CPU位置出现的，CPU处理速度非常快，相对CPU来说，去主内存获取数据这个事情太慢了，
 * CPU就提供了L1,l2,l3的三级缓存，每次去主内存拿完数据后，就会存储到CPU的三级缓存，每次去三级缓存
 * 拿数据，效率可定会提升
 * 这就带来了问题，现在CPU都是多核，每个线程的工作内存（CPU三级缓存）都是独立的，会告知每个线程中
 * 做修改时，只改自己的工作内存，没有及时的同步到主内存，导致数据不一致问题
 */
public class ThreadVisibility {

    public static void main(String[] args) throws InterruptedException {
        reproduceTheVisibilityIssue();
    }

    private static boolean flag = true;

    /**
     * 主线程和t1线程中使用的成员变量flag虽然是同一个，但在多核CPU内部，
     * 每个线程在调用其CPU都缓存了一份变量值在自己的三级缓存中，其缓存的值的更新互不影响，
     * 这里主线程的flag更新后，无法影响t1三级缓存的flag
     *
     * @throws InterruptedException e
     */
    public static void reproduceTheVisibilityIssue() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            //循环不会结束
            while (flag) {
                //do something
            }
            System.out.println("t1 thread end.");
        });
        t1.start();
        Thread.sleep(10); //此处睡10ms的目的为：防止t1线程还未启动，主线程就执行了flag=false导致while循环进不去
        flag = false;
        System.out.println("main thread update flag : " + flag);
    }

}

/**
 * 解决可见性问题
 */
class SolveVisibility {

    public static void main(String[] args) throws InterruptedException {
//        volatileSolveTheVisibilityIssue();
//        synchronizedSolveTheVisibilityIssue();
        lockSolveTheVisibilityIssue();
    }

    /**
     * 方式一：使用volatile修饰成员变量
     *
     * volatile是一个关键字，用来修饰成员变量
     * 如果属性被volatile修饰，相当于告诉CPU，对当前属性的操作，不允许使用CPU的缓存，必须去和主内存操作
     * volatile的内存语义：
     *  1.volatile属性被写：当写一个volatile变量，JMM会将当前线程对应的CPU缓存及时的刷新到主内存中
     *  2.volatile属性被读：当读一个volatile变量，JMM会将对应的eCPU缓存中的内存设置为无效，必须去主内存中重新读取共享变量
     * 其实加了volatile就是告诉CPU，对当前属性的读写操作，不允许使用CPU缓存，加了volatile修饰的属性，会在转为汇编指令之后，
     * 追加一个lock指令前缀，CPU执行这个指令时，如果带有lock前缀会做两个事情：
     *  1.将当前处理器缓存行的数据写回到主内存
     *  2.这个写回的数据，在其他的CPU内核的缓存中，直接无效
     *
     *  总结：volatile就是让CPU每次操作这个数据时，必须立即同步到主内存，以及从主内存读取数据
     */
    private static volatile boolean flag = true;

    public static void volatileSolveTheVisibilityIssue() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            //flag在赋值false后立即结束
            while (flag) {
                //do something
            }
            System.out.println("t1 thread end.");
        });
        t1.start();
        Thread.sleep(10); //此处睡10ms的目的为：防止t1线程还未启动，主线程就执行了flag=false导致while循环进不去
        flag = false;
        System.out.println("main thread update flag : " + flag);
    }

    /**
     * 方式二：synchronized
     * synchronized也是可以解决可见性问题的，synchronized的内存语义：
     * 如果涉及到了synchronized的同步代码块或者是同步方法，获取锁资源之后，将内部涉及到的变量从CPU缓存中移除，
     * 必须去主内存中重新拿数据，而且在释放锁之后，会立即将CPU缓存中的数据同步到主内存
     */
    private static boolean tag = true;

    public static void synchronizedSolveTheVisibilityIssue() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (tag) {
                //必须写在获取变量之后，synchronized获取到锁后会立刻将内部涉及到的变量CPU缓存中移除，再重主内存中重新拿数据
                //如果synchronized在加载变量之前，这时tag变量压根就还没加载到CPU缓存中，后面加载的数据还是主内存中获取tag=true
                synchronized (SolveVisibility.class) {
                    //do something
                }
            }
            System.out.println("t1 thread end.");
        });
        t1.start();
        Thread.sleep(10); //此处睡10ms的目的为：防止t1线程还未启动，主线程就执行了flag=false导致while循环进不去
        tag = false;
        System.out.println("main thread update flag : " + tag);
    }

    /**
     * 方式三：Lock锁保证可见性的方式和synchronized完全不同，synchronized基于他的内存语义，在获取锁和释放锁时，
     * 对CPU缓存做一个同步到内存的操作
     * Lock锁是基于volatile实现的。Lock锁内部在进行加锁和释放锁时，会对一个由volatile修饰的state属性进行加减操作
     * 如果volatile修饰的属性进行写操作，CPU会执行带有lock前缀的指令，CPU会将修改的数据，从CPU缓存立即同步到主内存，
     * 同时也会将其他的属性也立即同步到主内存中。还会将其他CPU缓存行中的这个数据设置为无效，必须重新从主内存中拉取
     */
    private static Lock lock = new ReentrantLock(); //实际底层源码使用了一个volatile修饰的变量state
    //等同于
    private static volatile int i;
    public static void lockSolveTheVisibilityIssue() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (tag) {
                /*lock.lock();
                try {
                    //....
                } finally {
                    lock.unlock();
                }*/
                //等同于
                i++;
                //do something
            }
            System.out.println("t1 thread end.");
        });
        t1.start();
        Thread.sleep(10); //此处睡10ms的目的为：防止t1线程还未启动，主线程就执行了flag=false导致while循环进不去
        tag = false;
        System.out.println("main thread update flag : " + tag);
    }

    /**
     * 方式四：final
     * final修饰的属性，在运行期间是不允许修改的，这样一来，就间接保证了可见性，所有多线程读取final属性，值肯定是一样的
     * final并不是说每次读取数据从主内存读取，他没有这个必要，而且final和volatile是不允许同时修饰一个属性的
     * final修饰的内容已经不允许再次被写了，而volatile是保证每次读写数据去主内存读取，并且volatile会影响一定的性能，就不需要同时修饰
     */
    private static final boolean tg = true;
}
