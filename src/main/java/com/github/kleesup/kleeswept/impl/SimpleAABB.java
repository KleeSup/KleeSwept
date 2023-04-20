package com.github.kleesup.kleeswept.impl;

import com.badlogic.gdx.math.Rectangle;
import com.github.kleesup.kleeswept.AABB;

/**
 * An implementation of {@link AABB} which does not require {@link Rectangle} from LibGDX and therefore doesn't require LibGDX.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleAABB implements AABB {

    private float x, y, width, height;
    public SimpleAABB(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public SimpleAABB(SimpleAABB aabb){
        this(aabb.x, aabb.y, aabb.width, aabb.height);
    }
    public SimpleAABB(Rectangle rectangle){
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    public SimpleAABB(){}

    /*
    Setter
    */

    public SimpleAABB setX(float x){
        this.x = x;
        return this;
    }
    public SimpleAABB setY(float y){
        this.y = y;
        return this;
    }
    public SimpleAABB setWidth(float width){
        this.width = width;
        return this;
    }
    public SimpleAABB setHeight(float height){
        this.height = height;
        return this;
    }

    /*
    Getter
    */

    public float getCenterX(){
        return x + (width/2f);
    }
    public float getCenterY(){
        return y + (height/2f);
    }

    public float getMaxX(){
        return x + width;
    }
    public float getMaxY(){
        return y + height;
    }

    @Override
    public SimpleAABB setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public SimpleAABB setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public SimpleAABB setCenter (float x, float y) {
        setPosition(x - width / 2, y - height / 2);
        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
