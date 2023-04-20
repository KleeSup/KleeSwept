package com.github.kleesup.kleeswept;

/**
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public interface AABB {

    float getX();
    float getY();
    float getWidth();
    float getHeight();
    float getCenterX();
    float getCenterY();
    float getMaxX();
    float getMaxY();

    <T> T setCenter(float x, float y);
    <T> T setPosition(float x, float y);

    <T> T setSize(float width, float height);

}
