package com.armin.thread.orderliness;

/**
 * 在Java中，.java文件中内容会被编译，在执行前需要再次转换为CPU可以识别的指令，CPU在执行这些指令时，为了提升执行效率，
 * 在不影响最终结果的前提下（满足一些要求），会对指令进行重排
 *
 * 指令乱序执行的原因，是为了尽可能的发挥CPU的性能
 *
 * Java中的程序是乱序执行的
 */
public class ThreadOrderliness {

    public static void main(String[] args) throws InterruptedException {
        verifyOutOfOrderExecution();
    }

    static int a,b,x,y;
    /**
     * 验证乱序执行
     */
    public static void verifyOutOfOrderExecution() throws InterruptedException {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            a = 0;
            b = 0;
            x = 0;
            y = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                x = b;
            });
            Thread t2 = new Thread(() -> {
                b = 1;
                y = a;
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            if (x == 0 && y == 0) {
                System.out.println("循环第" + i + "次，x=" + x + "，y=" + y);
            }
        }
    }
}

/**
 * 单例模式由于指令重排序可能会出现问题：
 * 线程可能会拿到没有初始化的对象，导致在使用时，可能由于内部属性为默认值，导致出现一些不必要问题
 * 解决方案：对单例返回的对象使用volatile修饰
 */
class SingletonPatternInstructionRearrangementProblem {

    private static volatile SingletonPatternInstructionRearrangementProblem INSTANCE;

    private SingletonPatternInstructionRearrangementProblem() {

    }

    public static SingletonPatternInstructionRearrangementProblem getInstance() {
        //B
        if (INSTANCE == null) {
            synchronized (SingletonPatternInstructionRearrangementProblem.class) {
                if (INSTANCE == null) {
                    //A 开辟空间，INSTANCE指向地址，初始化（没有volatile修饰）
                    //A 开辟空间，初始化，INSTANCE指向地址（volatile修饰INSTANCE）
                    INSTANCE = new SingletonPatternInstructionRearrangementProblem();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * as-if-serial（CPU级别）
     * as-if-serial语义：
     * 不论指定如何重排序，需要保证单线程的程序执行结果是不变的
     * 而且如果存在依赖关系，那么也不可以做指令重排
     */
    public static void main(String[] args) {
        //这种情况肯定不能做指令重排序
        int i = 0;
        i++;

        //这种情况肯定不能做指令重排序
        int j = 200;
        j *= 100;
        j += 100;
        //这里即使出现了指令重排，也不可影响最终结果，20100
        System.out.println(j);
    }

    /**
     * happens-before（虚拟机级别）
     * 具体规则：
     *      1.单线程happen-before原则：在同一个线程中，书写在前面的操作happen-before后面的操作
     *      2.锁的happen-before原则：同一个锁的unlock操作lock操作
     *      3.volatile的happen-before原则：对一个volatile变量的写操作happen-before对此变量的任务操作
     *      4.happen-before的传递性原则：如果A操作happen-before B操作，B操作happen-before C操作，那么A操作happen-before C操作
     *      5.线程启动的happen-before原则：同一个线程的start方法happen-before此线程的其他方法
     *      6.线程中断的happen-before原则：对线程interrupt方法的调用happen-before被中断线程的检测到中断发送的代码
     *      7.线程终结的happen-before原则：线程中所有操作都happen-before线程的终止检测
     *      8.对象创建的happen-before原则：一个对象的初始化完成先于他的finalize方法调用
     * JMM只有在不出现上述8种情况时，才不会触发指令重排效果
     *
     * 作为开发，不需要过分的关注happen-before原则，只需要可以写出线程安全的代码就可以了
     */

    /**
     * volatile
     * 如果需要让程序对某一个属性的操作不出现指令重排，除了满足happen-before原则之外，还可以基于volatile修饰属性，
     * 从而对这个属性的操作，就不会出现指令重排的问题了
     *
     * volatile如何实现的禁止指令重排？
     * 内存屏障概念。将内存屏障看成一条指令
     * 会在两个操作之间，添加上一道指令，这个指令就可以避免上下执行的其他指令进行重排序
     */

}