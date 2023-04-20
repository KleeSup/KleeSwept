package com.github.kleesup.kleeswept.impl;

import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.Magnitude;

/**
 * <br>Created on 17.04.2023</br>
 *
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class Vector2Magnitude extends Vector2 implements Magnitude {

    public Vector2Magnitude() {
    }

    public Vector2Magnitude(float x, float y) {
        super(x, y);
    }

    public Vector2Magnitude(Vector2 v) {
        super(v);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Vector2 setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public Vector2 setY(float y) {
        this.y = y;
        return this;
    }

}
