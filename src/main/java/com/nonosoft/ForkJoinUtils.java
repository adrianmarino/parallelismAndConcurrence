package com.nonosoft;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinUtils {

    public static <T> T submit(int poolSize, Callable<T> callable) throws ExecutionException, InterruptedException {
        return new ForkJoinPool(poolSize).submit(callable).get();
    }

    private ForkJoinUtils() {
    }
}
