package com.github.kleesup.kleeswept;

/**
 * <br>Created on 17.04.2023</br>
 *
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public interface Magnitude {

    float getX();
    float getY();
    <T> T setX(float x);
    <T> T setY(float y);
    <T> T set(float x, float y);
    <T> T add(float x, float y);
    <T> T sub(float x, float y);

}
