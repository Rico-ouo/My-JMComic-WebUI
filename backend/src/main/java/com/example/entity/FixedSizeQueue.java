package com.example.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedSizeQueue<T> {
    private final LinkedBlockingQueue<T> queue;
    private final int maxSize;

    public FixedSizeQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedBlockingQueue<>(maxSize);
    }

    public synchronized void add(T item) {
        while (queue.size() >= maxSize){
            queue.poll(); // 移除最旧的元素
        }
        queue.offer(item);
    }

    public List<T> getAll() {
        return new ArrayList<>(queue);
    }
}