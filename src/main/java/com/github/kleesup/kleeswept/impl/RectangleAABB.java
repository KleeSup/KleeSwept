package com.github.kleesup.kleeswept.impl;

import com.badlogic.gdx.math.Rectangle;
import com.github.kleesup.kleeswept.AABB;

/**
 * An implementation of {@link AABB} which uses the {@link Rectangle} class from LibGDX as base to append on functionality.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class RectangleAABB extends Rectangle implements AABB {

    public RectangleAABB() {
    }
    public RectangleAABB(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
    public RectangleAABB(Rectangle rect) {
        super(rect);
    }
    public RectangleAABB(SimpleAABB aabb){
        super(aabb.getX(), aabb.getY(), aabb.getWidth(), aabb.getHeight());
    }

    @Override
    public float getCenterX() {
        return x + (width/2f);
    }

    @Override
    public float getCenterY() {
        return y + (height/2f);
    }

    @Override
    public float getMaxX() {
        return x + width;
    }

    @Override
    public float getMaxY() {
        return y + height;
    }

    @Override
    public Rectangle setSize(float width, float height) {
        return super.setSize(width, height);
    }

    @Override
    public Rectangle setPosition(float x, float y) {
        return super.setPosition(x, y);
    }

    @Override
    public Rectangle setCenter(float x, float y) {
        return super.setCenter(x, y);
    }
}
