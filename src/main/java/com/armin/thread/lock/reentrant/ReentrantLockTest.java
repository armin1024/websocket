package com.armin.thread.lock.reentrant;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    public static void main(String[] args) {
        //默认是非公平锁
        ReentrantLock lock1 = new ReentrantLock(); //无参构造默认非公平锁
        //有参构造设置为true：公平锁
        ReentrantLock lock2 = new ReentrantLock(true);
        lock1.lock();
        try {
            System.out.println("业务代码");
        } finally {
            lock1.unlock();
        }
    }

}
