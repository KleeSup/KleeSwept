package com.github.kleesup.kleeswept.impl;

import com.github.kleesup.kleeswept.Magnitude;

/**
 * Simple implementation of {@link Magnitude} without the use of {@link com.badlogic.gdx.math.Vector2} from LibGDX.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class SweptMagnitude implements Magnitude {

    public float x, y;
    public SweptMagnitude(float x, float y){
        this.x = x;
        this.y = y;
    }
    public SweptMagnitude(SweptMagnitude magnitude){
        this.x = magnitude.x;
        this.y = magnitude.y;
    }
    public SweptMagnitude(){}

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public SweptMagnitude setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public SweptMagnitude setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public SweptMagnitude set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public SweptMagnitude add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public SweptMagnitude sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
}
