package com.armin.thread.pool;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorTest {

    public static void main(String[] args) {
        //1. 构建定时任务线程池
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(
                5,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        return t;
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        //2. 应用ScheduledThreadPoolExecutor
        // 跟直接执行线程池的execute没啥区别
        pool.execute(() -> {
            System.out.println("execute");
        });

        // 指定延迟时间执行
        System.out.println(System.currentTimeMillis());
        pool.schedule(() -> {
            System.out.println("schedule");
            System.out.println(System.currentTimeMillis());
        }, 2, TimeUnit.SECONDS);

        // 指定第一次的延迟时间，并且确认后期的周期执行时间，周期时间是在任务开始时就计算
        // 周期性执行就是将执行完毕的任务再次社会好延迟时间，并且重新扔到阻塞队列
        // 计算的周期执行，也是在原有的时间上做累加，不关注任务的执行时长。
        System.out.println(System.currentTimeMillis());
        pool.scheduleAtFixedRate(() -> {
            System.out.println("scheduleAtFixedRate");
            System.out.println(System.currentTimeMillis());
        }, 2, 3, TimeUnit.SECONDS);


        // 指定第一次的延迟时间，并且确认后期的周期执行时间，周期时间是在任务结束后再计算下次的延迟时间
        System.out.println(System.currentTimeMillis());
        pool.scheduleWithFixedDelay(() -> {
            System.out.println("scheduleWithFixedDelay");
            System.out.println(System.currentTimeMillis());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

}
