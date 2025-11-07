package com.arkanoid.utils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * là class hỗ trợ.
 * First in first out, kế thừa cấu trúc queue, có dung lượng xác đinh.
 */
public class FIFO<T> extends ArrayBlockingQueue<T> {
    private final int capacity;


    // ******************** Constructors **************************************

    /**
     * Hàm tạo.
     */
    public FIFO(final int capacity) {
        super(capacity);
        this.capacity = capacity;
    }


    // ******************** Methods *******************************************

    /**
     * Thêm phần tử.
     */
    @Override public boolean add(final T item) {
        if (this.size() == this.capacity) { remove(); }
        return super.add(item);
    }

    /**
     * Lấy danh sách phần tử.
     */
    public List<T> getItems() { return stream().toList(); }

    /**
     * Lấy phần tử tại vị trí cụ thể.
     */
    public T getItemAt(final int index) {
        if (index < 0 || index > size() - 1) { throw new IllegalArgumentException("Index out of bounds (0-" + (capacity - 1) + ")"); }
        return stream().toList().get(index);
    }

    @Override public String toString() {
        final StringBuilder msgBuilder = new StringBuilder(capacity);
        getItems().stream().forEach(item -> msgBuilder.append(item.toString()).append(","));
        msgBuilder.setLength(msgBuilder.length() - 1);
        return msgBuilder.toString();
    }
}
