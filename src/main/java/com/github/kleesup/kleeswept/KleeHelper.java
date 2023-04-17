package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Vector2;

/**
 * <br>Created on 17.04.2023</br>
 *
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public final class KleeHelper {

    private KleeHelper(){}

    /**
     * Throws a {@link IllegalArgumentException} when the specified object is {@code null}.
     * @param param The object to check for.
     * @param msg The message that is printed with the {@link IllegalArgumentException} if the object occurs to be {@code null}.
     * @throws IllegalArgumentException thrown when the specified object is {@code null}.
     */
    public static void paramRequireNonNull(Object param, String msg){
        if(param == null)throw new IllegalArgumentException(msg);
    }

    /**
     * Calculates the magnitude between two points.
     * @param x The x-coordinate of the start-position.
     * @param y The y-coordinate of the start-position.
     * @param goalX The x-coordinate of the goal-position.
     * @param goalY The y-coordinate of the goal-position.
     * @param vecWriteTo A temp response object where the outcome can be written into (Useful when less object creation is desired).
     * @return The calculated magnitude between the two points.
     */
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY, Vector2 vecWriteTo){
        return (vecWriteTo != null ? vecWriteTo : new Vector2()).set(goalX,goalY).sub(x,y);
    }
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY){
        return calculateMagnitude(x,y,goalX,goalY,null);
    }


}
