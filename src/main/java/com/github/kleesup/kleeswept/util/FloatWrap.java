package com.github.kleesup.kleeswept.util;

/**
 * This class is used to wrap a primitive {@code float} into a class where it can be gotten and set via methods.
 * <br>Created on 18.10.2023</br>
 * @author KleeSup
 * @version 1.1
 * @since 1.0
 */
public class FloatWrap {

    private float obj;
    public FloatWrap(float initialValue){
        this.obj = initialValue;
    }

    public float get() {
        return obj;
    }

    public void set(float t) {
        this.obj = t;
    }
}
