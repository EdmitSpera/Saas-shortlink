package com.nageoffer.shortlink.admin.test;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TTLExample {
    // 创建一个TransmittableThreadLocal变量
    private static TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        // 创建一个线程池
        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(3));


        // 提交一个任务到线程池
        executorService.submit(() -> {
            // 在线程池中获取ThreadLocal变量
            System.out.println("Thread Pool Task: " + context.get());
        });

        // 在主线程中设置ThreadLocal变量
        context.set("Main Thread Value");


        // 提交另一个任务到线程池
        executorService.submit(() -> {
            // 在线程池中获取ThreadLocal变量
            System.out.println("Thread Pool Task: " + context.get());
        });

        // 提交另一个任务到线程池
        executorService.submit(() -> {
            // 在线程池中获取ThreadLocal变量
            System.out.println("Thread Pool Task: " + context.get());
        });

        // 关闭线程池
        executorService.shutdown();
    }
}
