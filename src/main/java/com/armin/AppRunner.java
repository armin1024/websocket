package com.armin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class AppRunner {

    public static void main(String[] args) throws InterruptedException {
//        SpringApplication.run(AppRunner.class, args);
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                    Thread.sleep(1000);
                    System.out.println("t1 wait");
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(1000);
                System.out.println("t1 notify");
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(100);
    }

}
