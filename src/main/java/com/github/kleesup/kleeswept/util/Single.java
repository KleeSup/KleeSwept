package com.github.kleesup.kleeswept.util;

/**
 * <br>Created on 18.10.2023</br>
 * @author KleeSup
 * @version 1.0.1
 * @since 1.0
 */
public class Single<T> {

    private T obj;
    public Single(T initialValue){
        this.obj = initialValue;
    }

    public T get() {
        return obj;
    }

    public void set(T t) {
        this.obj = t;
    }
}
