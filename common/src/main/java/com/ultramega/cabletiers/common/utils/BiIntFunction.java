package com.ultramega.cabletiers.common.utils;

@FunctionalInterface
public interface BiIntFunction<R> {
    R apply(int a, int b);
}
