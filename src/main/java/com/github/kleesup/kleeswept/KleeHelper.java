package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


/**
 * Simple utility class.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.2
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
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY, Vector2 magWriteTo){
        Vector2 point = (magWriteTo != null ? magWriteTo : new Vector2()).set(goalX,goalY);
        point.sub(x,y);
        return point;
    }
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY){
        return calculateMagnitude(x,y,goalX,goalY,null);
    }

    /**
     * Calculates a summed AABB which represents an AABB centered at the center of the second AABB and the width and height of both combined.
     * @param aabb The AABB from which the sizes are added to the second.
     * @param other The second AABB which sets the position.
     * @param sumWriteTo A temp AABB object where the outcome can be written into (Useful when less object creation is desired).
     * @return The sum of both AABB sizes with the center located at the second AABBs center.
     */
    public static Rectangle calculateSumAABB(Rectangle aabb, Rectangle other, Rectangle sumWriteTo){
        paramRequireNonNull(aabb, "AABB cannot be null!");
        paramRequireNonNull(other, "Second AABB cannot be null!");
        if(sumWriteTo == null)sumWriteTo = new Rectangle();
        //calculating the sum of both AABBs by adding their width and height together.
        float width = aabb.getWidth() + other.getWidth();
        float height = aabb.getHeight() + other.getHeight();
        sumWriteTo.setSize(width, height);
        sumWriteTo.setCenter(KleeSweptDetection.getCenterX(other), KleeSweptDetection.getCenterY(other));
        return sumWriteTo;
    }
    public static Rectangle calculateSumAABB(Rectangle aabb, Rectangle other){
        return calculateSumAABB(aabb, other, null);
    }

    /**
     * Pairs two integers into a long.
     * @param x The first integer of the pair.
     * @param y The second integer of the pair.
     * @return The pair stored into a long.
     */
    public static long pairLong(int x, int y){
        return ((x & 0xFFFFFFFFL) | (y & 0xFFFFFFFFL) << 32);
    }

    /**
     * Retrieves the first integer of a pair.
     * @param pair The long where the integers were paired in.
     * @return The first integer if a pair.
     */
    public static int unpairIntY(long pair) {
        return (int) (pair >>> 32);
    }

    /**
     * Retrieves the second integer of a pair.
     * @param pair The long where the integers were paired in.
     * @return The second integer if a pair.
     */
    public static int unpairIntX(long pair) {
        return (int) pair;
    }

}
