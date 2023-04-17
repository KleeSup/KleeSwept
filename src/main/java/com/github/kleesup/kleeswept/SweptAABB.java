package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;

/**
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0.0
 * @since 1.0.0
 */
public class SweptAABB {

    private float x, y, width, height;
    public SweptAABB(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public SweptAABB(SweptAABB aabb){
        this(aabb.x, aabb.y, aabb.width, aabb.height);
    }
    public SweptAABB(Rectangle rectangle){
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    public SweptAABB(){
        this(0,0,0,0);
    }

    /*
    Setter
    */

    public SweptAABB setX(float x){
        this.x = x;
        return this;
    }
    public SweptAABB setY(float y){
        this.y = y;
        return this;
    }
    public SweptAABB setWidth(float width){
        this.width = width;
        return this;
    }
    public SweptAABB setHeight(float height){
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
