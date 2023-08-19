package com.armin.thread.lock.readwrite;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {

    static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    static ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            readLock.lock();
            try {
                System.out.println("child1 thread!");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                readLock.unlock();
            }
        }).start();

        Thread.sleep(100); //保证子线程先执行
        writeLock.lock();
        try {
            System.out.println("main thread!");
        } finally {
            writeLock.unlock();
        }
    }

    /*
        都是readLock：子线程和主线程基本上会同时打印
        都是writeLock：互斥访问，待子线程释放锁后主线程才会拿到锁继续执行
        readLock与writeLock并存：互斥访问，本质上获取的都是同一把lock锁，先要待子线程释放读锁后写锁才能拿到锁资源继续执行
            连续多个线程使用readLock可并发执行，直到遇见writeLock进行互斥访问
     */
}
