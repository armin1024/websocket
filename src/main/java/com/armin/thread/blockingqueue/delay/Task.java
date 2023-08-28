package com.armin.thread.blockingqueue.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Task implements Delayed {
    /** 任务的名称 */
    private String name;
    /** 什么时间点执行（单位：ms） */
    private Long time;

    public Task(String name, Long time) {
        this.name = name;
        this.time = System.currentTimeMillis() + time;
    }

    /**
     * 设置任务什么时候可以出延迟队列
     *
     * @param unit TimeUnit
     * @return 小于0代表可以被取出了
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 单位是毫秒
        return unit.convert(this.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 两个任务在插入到延迟队列时的比较方式
     *
     * @param o 被比较对象
     * @return 小于0，等于0，大于0
     */
    @Override
    public int compareTo(Delayed o) {
        return (int) (this.time - ((Task) o).getTime());
//        return (int) (((Task) o).getTime() - this.time);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", time=" + time +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
