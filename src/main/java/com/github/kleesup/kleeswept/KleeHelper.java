package com.github.kleesup.kleeswept;

import com.github.kleesup.kleeswept.impl.SimpleAABB;
import com.github.kleesup.kleeswept.impl.SweptMagnitude;

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
     * @param magWriteTo A temporary point object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The calculated magnitude between the two points.
     */
    public static Magnitude calculateMagnitude(float x, float y, float goalX, float goalY, Magnitude magWriteTo){
        Magnitude point = (magWriteTo != null ? magWriteTo : new SweptMagnitude()).set(goalX,goalY);
        point.sub(x,y);
        return point;
    }
    public static Magnitude calculateMagnitude(float x, float y, float goalX, float goalY){
        return calculateMagnitude(x,y,goalX,goalY,null);
    }

    /**
     * Calculates a summed AABB which represents an AABB centered at the center of the second AABB and the width and height of both combined.
     * @param aabb The AABB from which the sizes are added to the second.
     * @param other The second AABB which sets the position.
     * @param sumWriteTo A temp AABB object where the outcome can be written into (Useful when less object creation is desired).
     * @return The sum of both AABB sizes with the center located at the second AABBs center.
     */
    public static AABB calculateSumAABB(AABB aabb, AABB other, AABB sumWriteTo){
        paramRequireNonNull(aabb, "AABB cannot be null!");
        paramRequireNonNull(other, "Second AABB cannot be null!");
        if(sumWriteTo == null)sumWriteTo = new SimpleAABB();
        //calculating the sum of both AABBs by adding their width and height together.
        float width = aabb.getWidth() + other.getWidth();
        float height = aabb.getHeight() + other.getHeight();
        sumWriteTo.setSize(width, height);
        sumWriteTo.setCenter(other.getCenterX(), other.getCenterY());
        return sumWriteTo;
    }
    public static AABB calculateSumAABB(AABB aabb, AABB other){
        return calculateSumAABB(aabb, other, null);
    }

}
