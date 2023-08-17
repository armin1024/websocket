package com.armin.thread.lock.reentrant;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionObjectTest {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        new Thread(() -> {
            lock.lock();
            System.out.println("子线程获取锁资源并await挂起线程");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("子线程挂起后被唤醒！持有锁资源");
        }).start();
        Thread.sleep(100); //防止主线程先被执行
        //------------------main-----------------
        lock.lock();
        System.out.println("主线程等待5s拿到锁资源，子线程执行了await方法");
        //唤醒子线程，并到AQS队列中排队
        condition.signal();
        System.out.println("主线程唤醒了await挂起的子线程");
        //释放锁资源，AQS队列中的子线程得以拿到锁资源继续执行
        lock.unlock();
    }

}
