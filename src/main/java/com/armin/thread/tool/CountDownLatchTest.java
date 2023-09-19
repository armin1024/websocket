package com.armin.thread.tool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CountDownLatchTest {

    static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    static CountDownLatch countDownLatch = new CountDownLatch(3);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("主线程开始执行");
        sleep(1000);
        executor.execute(CountDownLatchTest::a);
        executor.execute(CountDownLatchTest::b);
        executor.execute(CountDownLatchTest::c);
        // 关闭线程池，让主线程执行完毕后退出
        executor.shutdown();
        System.out.println("三个任务并行执行,主业务线程等待");
        // 死等任务结束
        countDownLatch.await();
        // 如果在规定时间内，任务没有结束，返回false
        if (countDownLatch.await(5, TimeUnit.SECONDS)) {
            System.out.println("三个任务处理完毕，主业务线程继续执行");
        } else {
            System.out.println("三个任务没有全部处理完毕，执行其他的操作");
        }
    }

    private static void a() {
        System.out.println("A方法开始执行");
        sleep(1000);
        System.out.println("A方法执行结束");
        countDownLatch.countDown();
    }

    private static void b() {
        System.out.println("B方法开始执行");
        sleep(1500);
        System.out.println("B方法执行结束");
        countDownLatch.countDown();
    }

    private static void c() {
        System.out.println("C方法开始执行");
        sleep(2000);
        System.out.println("C方法执行结束");
        countDownLatch.countDown();
    }

    private static void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
