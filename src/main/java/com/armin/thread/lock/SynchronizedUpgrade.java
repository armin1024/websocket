package com.armin.thread.lock;

import org.openjdk.jol.info.ClassLayout;

/**
 * synchronized的锁升级
 */
public class SynchronizedUpgrade {

    public static void main(String[] args) throws InterruptedException {
        Object o = new Object();
        //匿名偏向锁
        System.out.println(ClassLayout.parseInstance(o).toPrintable()); //匿名偏向锁(101，后3字节未指向)

        new Thread(() -> {
            synchronized (o) {
                //t1: 偏向锁（可能）-> 轻量级锁（可能） -> 重量级锁（可能）
                System.out.println("t1:" + ClassLayout.parseInstance(o).toPrintable());
            }
        }).start();

        synchronized (o) {
            //main: 偏向锁（可能）-> 轻量级锁（可能） -> 重量级锁（可能）
            System.out.println("main:" + ClassLayout.parseInstance(o).toPrintable());//偏向锁(101，后3字节有指向)
        }
    }

}
