package com.armin.thread.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewScheduledThreadPool {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        //正常执行（与其他线程池执行效果无异）
        executor.execute(() -> {
            System.out.println("任务1：" + Thread.currentThread().getName() + "_" + System.currentTimeMillis());
        });

        //延迟执行，执行当前任务延迟5s后再执行
        executor.schedule(() -> {
            System.out.println("任务2：" + Thread.currentThread().getName() + "_" + System.currentTimeMillis());
        }, 5, TimeUnit.SECONDS);

        //周期执行，当前任务第一次延迟2s执行，然后每1s执行一次
        //这个方法在计算下次执行时间时，是从任务刚刚开始时就计算
        executor.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务3：" + Thread.currentThread().getName() + "_" + System.currentTimeMillis());
        }, 2, 1, TimeUnit.SECONDS);

        //周期执行，当前任务第一次延迟2s执行，然后每1s执行一次
        //这个方法在计算下次执行时间时，会等待任务结束后，再计算时间（这里是1s）
        executor.scheduleWithFixedDelay(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务4：" + Thread.currentThread().getName() + "_" + System.currentTimeMillis());
        }, 2, 1, TimeUnit.SECONDS);
    }

}
