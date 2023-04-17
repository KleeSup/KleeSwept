package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.response.SweptResponse;

/**
 *
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class KleeSweptCollisions {

    /*
    Simple intersection checks
    */

    public static boolean doesPointIntersectAABB(SweptAABB aabb, float pointX, float pointY){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        return pointX >= aabb.getX() &&
                pointX <= aabb.getMaxX() &&
                pointY >= aabb.getY() &&
                pointY <= aabb.getMaxY();
    }

    public static boolean doesPointIntersectAABB(SweptAABB aabb, Vector2 point){
        KleeHelper.paramRequireNonNull(point, "Point cannot be null!");
        return doesPointIntersectAABB(aabb, point.x, point.y);
    }

    public static boolean doesAABBIntersectAABB(SweptAABB aabb, SweptAABB other){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        return aabb.getX() < other.getX() + other.getWidth() && aabb.getX() + aabb.getWidth() > other.getX() &&
                aabb.getY() < other.getY() + other.getHeight() && aabb.getY() + aabb.getHeight() > other.getY();
    }

    public static boolean doesAABBContainAABB(SweptAABB aabb, SweptAABB other){
        return ((other.getX() > aabb.getX() && other.getX() < aabb.getX() + aabb.getWidth()) &&
                (other.getMaxX() > aabb.getX() && other.getMaxX() < aabb.getX() + aabb.getWidth()))
                && ((other.getY() > aabb.getY() && other.getY() < aabb.getX() + aabb.getHeight()) &&
                (other.getMaxY() > aabb.getX() && other.getMaxY() < aabb.getX() + aabb.getHeight()));
    }

    public static boolean doesAABBContainCircle(SweptAABB aabb, float circleX, float circleY, float radius){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        return (circleX - radius >= aabb.getX()) && (circleX + radius <= aabb.getX() + aabb.getWidth()) && (circleY - radius >= aabb.getY())
                && (circleY + radius <= aabb.getY() + aabb.getHeight());
    }

    public static boolean doesAABBContainCircle(SweptAABB aabb, Circle circle){
        KleeHelper.paramRequireNonNull(circle, "Circle cannot be null!");
        return doesAABBContainCircle(aabb, circle.x, circle.y, circle.radius);
    }

    /*
    Ray AABB intersection
    */


    /**
     * Does a swept collision test between a ray and an AABB.
     * @param x The x-coordinate of the ray (starting) position.
     * @param y The y-coordinate of the ray (starting) position.
     * @param goalX The x-coordinate of the goal position where the ray is supposed to end.
     * @param goalY The y-coordinate of the goal position where the ray is supposed to end.
     * @param aabb The AABB to test against.
     * @param responseWriteTo A temporary response object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWriteTo A temporary vector object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SweptResponse doesRayIntersectAABB(float x, float y, float goalX, float goalY, SweptAABB aabb, SweptResponse responseWriteTo, Vector2 tempMagWriteTo){
        Vector2 magnitude = tempMagWriteTo != null ? tempMagWriteTo : new Vector2();
        KleeHelper.calculateMagnitude(x,y,goalX,goalY,magnitude);
        return doesRayIntersectAABB(x,y,magnitude,aabb,responseWriteTo);
    }
    public static SweptResponse doesRayIntersectAABB(float x, float y, float goalX, float goalY, SweptAABB aabb, SweptResponse responseWriteTo){
        return doesRayIntersectAABB(x,y,goalX,goalY,aabb,responseWriteTo,null);
    }
    public static SweptResponse doesRayIntersectAABB(float x, float y, float goalX, float goalY, SweptAABB aabb){
        return doesRayIntersectAABB(x,y,goalX,goalY,aabb,null);
    }

    /**
     * Does a swept collision test between a ray and an AABB.
     * @param x The x-coordinate of the ray (starting) position.
     * @param y The y-coordinate of the ray (starting) position.
     * @param magnitude The magnitude (direction) of the ray.
     * @param aabb The AABB to test against.
     * @param responseWriteTo A temp response object where the outcome can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SweptResponse doesRayIntersectAABB(float x, float y, Vector2 magnitude, SweptAABB aabb, SweptResponse responseWriteTo){
        KleeHelper.paramRequireNonNull(magnitude, "Magnitude cannot be null!");
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        SweptResponse response = responseWriteTo != null ? responseWriteTo : new SweptResponse();
        response.reset();
        response.aabb = aabb;

        float lastEntry = Float.NEGATIVE_INFINITY;
        float firstExit = Float.POSITIVE_INFINITY;

        //magnitude should not be zero to avoid dividing by zero
        if(magnitude.x != 0){
            float t1 = (aabb.getX() - x) / magnitude.x;
            float t2 = (aabb.getMaxX() - x) / magnitude.x;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (x <= aabb.getX() || x >= aabb.getMaxX()){
            return response;
        }
        if(magnitude.y != 0){
            float t1 = (aabb.getY() - y) / magnitude.y;
            float t2 = (aabb.getMaxY() - y) / magnitude.y;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (y <= aabb.getY() || y >= aabb.getMaxY()){
            return response;
        }

        //condition for a collision
        if(firstExit > lastEntry && firstExit > 0 && lastEntry < 1){
            response.x = x + magnitude.x * lastEntry;
            response.y = y + magnitude.y * lastEntry;

            response.isHit = true;
            response.time = lastEntry;

            //calculating hit normal
            float dx = response.x - aabb.getCenterX();
            float dy = response.y - aabb.getCenterY();
            float px = (aabb.getWidth()/2) - Math.abs(dx);
            float py = (aabb.getHeight()/2) - Math.abs(dy);

            //calculating hit normal
            if(px < py){
                response.normalX = (dx > 0 ? 1 : 0) - (dx < 0 ? 1 : 0);
            }else{
                response.normalY = (dy > 0 ? 1 : 0) - (dy < 0 ? 1 : 0);
            }
        }
        return response;
    }
    public static SweptResponse doesRayIntersectAABB(float x, float y, Vector2 magnitude, SweptAABB aabb){
        return doesRayIntersectAABB(x,y,magnitude,aabb,null);
    }

    /*
    Swept AABB collision detection
    */



}
