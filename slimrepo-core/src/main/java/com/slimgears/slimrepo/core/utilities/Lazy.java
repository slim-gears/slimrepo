package com.slimgears.slimrepo.core.utilities;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class Lazy<T> implements Supplier<T> {
    private final AtomicReference<T> instance = new AtomicReference<>();
    private final Callable<T> supplier;

    public static <T> Supplier<T> of(Callable<T> supplier) {
        return new Lazy<>(supplier);
    }

    private Lazy(Callable<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return Optional
                .ofNullable(instance.get())
                .orElseGet(() -> {
                    synchronized (instance) {
                        return Optional.ofNullable(instance.get())
                                .orElseGet(this::createInstance);
                    }
                });
    }

    private T createInstance() {
        try {
            T obj = supplier.call();
            instance.lazySet(obj);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
