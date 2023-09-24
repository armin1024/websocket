package com.armin.thread.tool;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) throws IOException {

//        testSupplyAsync();
//        testRunAsync();
//        testThenApply();
//        testThenAccept();
//        testThenRun();
//        testThenCombine();
//        testThenAcceptBoth();
//        testRunAfterBoth();
//        testApplyEither();
//        testExceptionally();
//        testWhenComplete();
//        testHandle();
//        testAllOf();
        testAnyOf();
    }

    private static void testAnyOf() {
        long start = System.currentTimeMillis();
        CompletableFuture<Void> task = CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务A");
                    return "A";
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务B");
                    return "B";
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务C");
                    return "C";
                })
        ).thenAccept(r -> {
            System.out.println("任务D执行，" + r + "先执行完毕的");
        });
        System.out.println(task.join()); //阻塞主线程等待task任务执行完毕，便于查看执行过程
        System.out.println("执行时长:" + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testAllOf() {
        long start = System.currentTimeMillis();
        CompletableFuture<Void> task = CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务A");
                }),
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务B");
                }),
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("任务C");
                })
        ).thenRun(() -> {
            System.out.println("任务D");
        });
        System.out.println(task.join()); //阻塞主线程等待task任务执行完毕，便于查看执行过程
        System.out.println("执行时长:" + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testHandle() {
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A, " + Thread.currentThread().getName());
            int i = 1 / 0;
            return 88;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            System.out.println("任务B, " + Thread.currentThread().getName());
            try {
                Thread.sleep(10); // 保证任务A先执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 77;
        }), resultFirst -> {
            System.out.println("任务C，获取到值：" + resultFirst + " " + Thread.currentThread().getName());
            return resultFirst;
        }).handle((r, e) -> {
            System.out.println("获取到结果：" + r);
            System.out.println("获取到异常, " + e);
            System.out.println("这里会被执行");
            return -1;
        });
        System.out.println("执行结果, " + task.join());
    }

    private static void testWhenComplete() {
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A, " + Thread.currentThread().getName());
            int i = 1 / 0;
            return 88;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            System.out.println("任务B, " + Thread.currentThread().getName());
            return 77;
        }), resultFirst -> {
            System.out.println("任务C，获取到值：" + resultFirst + " " + Thread.currentThread().getName());
            return resultFirst;
        }).whenComplete((r, e) -> {
            System.out.println("获取到结果：" + r);
            System.out.println("获取到异常, " + e);
            System.out.println("这里会被执行");
        });
        System.out.println("执行结果, " + task.join());
    }

    private static void testExceptionally() {
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A, " + Thread.currentThread().getName());
            int i = 1 / 0;
            return 88;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            System.out.println("任务B, " + Thread.currentThread().getName());
            return 77;
        }), resultFirst -> {
            System.out.println("任务C，获取到值：" + resultFirst + " " + Thread.currentThread().getName());
            return resultFirst;
        }).exceptionally(e -> {
            // exception
            System.out.println("获取到异常, " + e.getMessage());
            return -1;
        });
        System.out.println("执行结果, " + task.join());
    }

    private static void testApplyEither() {
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A, " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 88;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            System.out.println("任务B, " + Thread.currentThread().getName());
            return 77;
        }), result -> {
            System.out.println("任务C，获取到值：" + result + " " + Thread.currentThread().getName());
            return result;
        });
        System.out.println(task.join());
    }

    private static void testRunAfterBoth() throws IOException {
        CompletableFuture
                .supplyAsync(() -> "a")
                .runAfterBoth(CompletableFuture.supplyAsync(() -> "b"), () -> {
                    System.out.println("do something");
                });
    }

    private static void testThenAcceptBoth() {
        CompletableFuture
                .supplyAsync(() -> "Hello CompletableFuture!")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> "abc"), (a, b) -> {
                    System.out.println(a + " " + b);
                });
    }

    //thenCombine
    private static void testThenCombine() {
        long start = System.currentTimeMillis();
        CompletableFuture<Integer> taskC = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程A：" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 50;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            System.out.println("线程B:" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 49;
        }), (resA, resB) -> {
            System.out.println("线程C:" + Thread.currentThread().getName());
            return resA + resB;
        });
        System.out.println(taskC.join());
        System.out.println("执行时间：" + (System.currentTimeMillis() - start) + "ms");
    }

    //thenRun没有返回值
    private static void testThenRun() throws IOException {
//        CompletableFuture.runAsync(() -> {
//            System.out.println("任务A！");
//        }).thenRun(() -> {
//            System.out.println("任务B！");
//        });

        CompletableFuture.runAsync(() -> {
            System.out.println("任务A！");
        }).thenRunAsync(() -> {
            System.out.println("任务B！");
        });
        System.in.read();
    }

    //thenAccept拿到上一个任务的返回值为入参做操作，不做返回。thenAcceptAsync可接收线程池
    private static void testThenAccept() {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A");
            return "abc";
        }).thenAccept(result -> {
            System.out.println("任务B输出任务A返回：" + result);
        });
    }

    //第一个线程执行后第二个线程拿到第一个线程的结果后继续执行
    private static void testThenApply() {
        //way 1
        /*CompletableFuture<String> taskA = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        });
        CompletableFuture<String> taskB = taskA.thenApply(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = taskB.join();
        System.out.println("main get B result:" + result);
        */
        //another way
        /*CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        }).thenApply(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = task.join();
        System.out.println("main get B result:" + result);*/

        //another way 可以使用线程池异步执行
        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        }).thenApplyAsync(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = task.join();
        System.out.println("main get B result:" + result);
    }

    //线程无返回值，与普通线程无异
    private static void testRunAsync() throws IOException {
        CompletableFuture.runAsync(() -> {
            System.out.println("任务go");
            System.out.println("任务done");
        });

        System.in.read();
    }

    //线程有返回值
    private static void testSupplyAsync() {
        // 生产者，可以指定返回结果
        CompletableFuture<String> firstTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务开始执行");
            System.out.println("异步任务执行结束");
            return "返回结果";
        });

        String result1 = firstTask.join();
        String result2 = null;
        try {
            result2 = firstTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(result1 + "," + result2);
    }

}
