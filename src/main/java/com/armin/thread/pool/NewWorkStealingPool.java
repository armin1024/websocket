package com.armin.thread.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class NewWorkStealingPool {

    /** 非常大的数组 */
    static int[] nums = new int[1_000_000_000];
    // 填充值
    static{
        for (int i = 0; i < nums.length; i++) {
            nums[i] = (int) ((Math.random()) * 1000);
        }
    }
    public static void main(String[] args) {
        // ===================单线程累加10亿数据================================
        System.out.println("单线程计算数组总和！");
        long start = System.nanoTime();
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        long end = System.nanoTime();
        System.out.println("单线程运算结果为：" + sum + "，计算时间为：" + (end  - start));

        // ===================多线程分而治之累加10亿数据================================
        // 在使用forkJoinPool时，不推荐使用Runnable和Callable
        // 可以使用提供的另外两种任务的描述方式
        // Runnable(没有返回结果) ->   RecursiveAction
        // Callable(有返回结果)   ->   RecursiveTask
        ForkJoinPool forkJoinPool = (ForkJoinPool) Executors.newWorkStealingPool();
        System.out.println("分而治之计算数组总和！");
        long forkJoinStart = System.nanoTime();
        ForkJoinTask<Integer> task = forkJoinPool.submit(new SumRecursiveTask(0, nums.length - 1));
        Integer result = task.join();
        long forkJoinEnd = System.nanoTime();
        System.out.println("分而治之运算结果为：" + result + "，计算时间为：" + (forkJoinEnd  - forkJoinStart));
    }

    private static class SumRecursiveTask extends RecursiveTask<Integer> {
        /** 指定一个线程处理哪个位置的数据 */
        private int start,end;
        private final int MAX_STRIDE = 300_000_000;
        //  200_000_000： 147964900
        //  100_000_000： 145942100

        public SumRecursiveTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            // 在这个方法中，需要设置好任务拆分的逻辑以及聚合的逻辑
            int sum = 0;
            int stride = end - start;
            if(stride <= MAX_STRIDE){
                // 可以处理任务
                for (int i = start; i <= end; i++) {
                    sum += nums[i];
                }
            }else{
                // 将任务拆分，分而治之。
                int middle = (start + end) / 2;
                // 声明为2个任务
                SumRecursiveTask left = new SumRecursiveTask(start, middle);
                SumRecursiveTask right = new SumRecursiveTask(middle + 1, end);
                // 分别执行两个任务
                left.fork();
                right.fork();
                // 等待结果，并且获取sum
                sum = left.join() + right.join();
            }
            return sum;
        }
    }

}
