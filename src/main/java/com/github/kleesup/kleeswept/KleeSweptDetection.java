package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.util.BytePair;
import com.github.kleesup.kleeswept.util.FloatWrap;

import java.util.*;

/**
 *
 * <br>Created on 22.04.2023</br>
 * @author KleeSup
 * @version 1.4
 * @since 1.0.0
 */
public class KleeSweptDetection {

    /** A small offset value to reduce floating point imprecision. Defaults to {@code 1e-5f}. */
    public static float DELTA = 1e-5f;

    /*
    Specified getters for AABBs.
    */

    public static float getCenterX(Rectangle aabb){
        return aabb.getX() + (aabb.getWidth() * 0.5f);
    }
    public static float getCenterY(Rectangle aabb){
        return aabb.getY() + (aabb.getHeight() * 0.5f);
    }

    public static float getMaxX(Rectangle aabb){
        return aabb.getX() + aabb.getWidth();
    }
    public static float getMaxY(Rectangle aabb){
        return aabb.getY() + aabb.getHeight();
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
     * @param hitPosition Required to write the ray-hit position.
     * @param tempMagWriteTo A temporary vector object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static boolean doesRayIntersectAABB(float x, float y, float goalX, float goalY, Rectangle aabb, Vector2 hitPosition, BytePair normal, Vector2 tempMagWriteTo){
        Vector2 magnitude = tempMagWriteTo != null ? tempMagWriteTo : new Vector2();
        KleeHelper.calculateMagnitude(x,y,goalX,goalY,magnitude);
        return doesRayIntersectAABB(x,y,magnitude,aabb,hitPosition,normal,null);
    }
    public static boolean doesRayIntersectAABB(float x, float y, float goalX, float goalY, Rectangle aabb, Vector2 hitPosition, BytePair normal){
        return doesRayIntersectAABB(x,y,goalX,goalY,aabb,hitPosition, normal, null);
    }

    /**
     * Does a swept collision test between a ray and an AABB.
     * @param x The x-coordinate of the ray (starting) position.
     * @param y The y-coordinate of the ray (starting) position.
     * @param magnitude The magnitude (size) of the ray.
     * @param aabb The AABB to test against.
     * @param hitPosition Required to write the ray-hit position.
     * @param normal Required to write the ray-hit normal.
     * @return Whether the ray hit the AABB.
     */
    public static boolean doesRayIntersectAABB(float x, float y, Vector2 magnitude, Rectangle aabb, Vector2 hitPosition, BytePair normal, FloatWrap outHitTime){
        float lastEntry = Float.NEGATIVE_INFINITY;
        float firstExit = Float.POSITIVE_INFINITY;

        //magnitude should not be zero to avoid dividing by zero
        if(magnitude.x != 0){
            float inv = 1f / magnitude.x;
            float t1 = (aabb.getX() - x) * inv;
            float t2 = (getMaxX(aabb) - x) * inv;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (x <= aabb.getX() || x >= getMaxX(aabb)){
            return false;
        }
        if(magnitude.y != 0){
            float inv = 1f / magnitude.y;
            float t1 = (aabb.getY() - y) * inv;
            float t2 = (getMaxY(aabb) - y) * inv;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (y <= aabb.getY() || y >= getMaxY(aabb)){
            return false;
        }

        //condition for a collision
        if(firstExit > lastEntry &&
                firstExit > 0 &&
                lastEntry < 1 &&
                Math.abs(lastEntry - firstExit) >= DELTA && //imprecision detection
                (lastEntry >= 0 || Math.abs(lastEntry) < DELTA)){
            if(hitPosition == null)hitPosition = new Vector2();
            hitPosition.set(x + magnitude.x * lastEntry, y + magnitude.y * lastEntry);

            if(outHitTime != null)outHitTime.set(lastEntry);

            //calculating hit normal
            if(normal != null){
                normal.setZero();

                float dx = hitPosition.x - getCenterX(aabb);
                float dy = hitPosition.y - getCenterY(aabb);
                float px = (aabb.getWidth() * .5f) - Math.abs(dx);
                float py = (aabb.getHeight() * .5f) - Math.abs(dy);

                //calculating hit normal
                if(px < py){
                    normal.x = (byte) ((dx > 0 ? 1 : 0) - (dx < 0 ? 1 : 0));
                }else{
                    normal.y = (byte) ((dy > 0 ? 1 : 0) - (dy < 0 ? 1 : 0));
                }
            }
            return true;
        }
        return false;
    }

    /*
    Swept AABB collision detection
    - dynamic vs static
    */

    /**
     * Checks for a collision between a dynamic and a static AABB.
     * @param dynamicBox The dynamic aabb.
     * @param staticBox The static aabb.
     * @param displacement The displacement of the dynamic aabb.
     * @param normal Required to write the hit normal.
     * @param tempSum A temporary rectangle object where the summed AABB can be written into (Useful when less object creation is desired).
     * @param tempRayHit A temporary vector object where the ray-hit result can be written into (Useful when less object creation is desired).
     * @return {@code true} if the dynamic aabb collides with the static aabb, {@code false} otherwise.
     */
    public static boolean checkDynamicVsStatic(Rectangle dynamicBox, Rectangle staticBox, Vector2 displacement, BytePair normal,
                                               Rectangle tempSum, Vector2 tempRayHit, FloatWrap outHitTime){
        if(displacement == null)displacement = new Vector2();
        KleeHelper.calculateSumAABB(dynamicBox, staticBox, tempSum);

        normal = normal == null ? new BytePair() : normal.setZero();
        return doesRayIntersectAABB(getCenterX(dynamicBox), getCenterY(dynamicBox), displacement, tempSum, tempRayHit, normal, outHitTime);
    }
    public static boolean checkDynamicVsStatic(Rectangle dynamicBox, Rectangle staticBox, Vector2 displacement, BytePair normal){
        return checkDynamicVsStatic(dynamicBox, staticBox, displacement, normal, null, null,null);
    }

    /**
     * Checks for a collision between a dynamic and multiple static AABBs.
     * @param dynamicBox The dynamic aabb.
     * @param staticBoxes The collection of static AABBs.
     * @param displacement The displacement of the dynamic aabb.
     * @param tempNormal A temporary vector object where the hit-normal can be written into (Useful when less object creation is desired).
     * @param tempSum A temporary rectangle object where the summed AABB can be written into (Useful when less object creation is desired).
     * @param tempRayHit A temporary vector object where the ray-hit result can be written into (Useful when less object creation is desired).
     * @return A collection of all the static rectangles that are hit.
     */
    public static Collection<Rectangle> checkDynamicVsMultipleStatic(Rectangle dynamicBox, Collection<Rectangle> staticBoxes, Vector2 displacement, BytePair tempNormal,
                                                       Rectangle tempSum, Vector2 tempRayHit){
        if(staticBoxes == null || staticBoxes.isEmpty())return staticBoxes != null ? staticBoxes : new ArrayList<>();
        if(tempNormal == null)tempNormal = new BytePair();
        if(tempSum == null)tempSum = new Rectangle();
        if(tempRayHit == null)tempRayHit = new Vector2();
        Iterator<Rectangle> itr = staticBoxes.iterator();
        while (itr.hasNext()){
            Rectangle staticBox = itr.next();
            if(!checkDynamicVsStatic(dynamicBox, staticBox, displacement, tempNormal, tempSum, tempRayHit, null)){
                itr.remove();
            }
        }
        return staticBoxes;
    }
    public static Collection<Rectangle> checkDynamicVsMultipleStatic(Rectangle dynamicBox, Collection<Rectangle> staticBoxes, Vector2 displacement){
        return checkDynamicVsMultipleStatic(dynamicBox, staticBoxes, displacement, null, null, null);
    }

    /*
    Swept AABB collision detection
    - dynamic vs dynamic
    */

    /**
     * Checks for a collision between a dynamic and another dynamic AABB.
     * The result goes for the first aabb hitting the second so hit-normal will be the normal at the second AABB.
     * @param firstBox The first dynamic aabb.
     * @param firstDisplacement The displacement of the first dynamic aabb.
     * @param secondBox The second dynamic aabb.
     * @param secondDisplacement The displacement of the second dynamic aabb.
     * @param normal Required to write the hit normal.
     * @param tempSum A temporary rectangle object where the summed AABB can be written into (Useful when less object creation is desired).
     * @param tempRayHit A temporary vector object where the ray-hit result can be written into (Useful when less object creation is desired).
     * @return {@code true} if the both AABBs collide with each other, {@code false} otherwise.
     */
    public static boolean checkDynamicVsDynamic(Rectangle firstBox, Vector2 firstDisplacement, Rectangle secondBox, Vector2 secondDisplacement, BytePair normal,
                                               Rectangle tempSum, Vector2 tempRayHit){
        firstDisplacement.set(firstDisplacement.x - secondDisplacement.x, firstDisplacement.y - secondDisplacement.y);
        return checkDynamicVsStatic(firstBox, secondBox, firstDisplacement, normal, tempSum, tempRayHit, null);
    }
    public static boolean checkDynamicVsDynamic(Rectangle firstBox, Vector2 firstDisplacement, Rectangle secondBox, Vector2 secondDisplacement, BytePair normal){
        return checkDynamicVsDynamic(firstBox, firstDisplacement, secondBox, secondDisplacement, normal, null, null);
    }

    /**
     * Checks for a collision between a dynamic and multiple dynamic AABBs.
     * @param firstBox The first dynamic aabb.
     * @param firstDisplacement The displacement of the first dynamic aabb.
     * @param otherBoxes The collection of dynamic aabbs with their displacement.
     * @param tempNormal A temporary vector object where the hit-normal can be written into (Useful when less object creation is desired).
     * @param tempSum A temporary rectangle object where the summed AABB can be written into (Useful when less object creation is desired).
     * @param tempRayHit A temporary vector object where the ray-hit result can be written into (Useful when less object creation is desired).
     * @return A map containing all the dynamic aabbs that were hit (with their displacements).
     */
    public static Map<Rectangle, Vector2> checkDynamicVsMultipleDynamic(Rectangle firstBox, Vector2 firstDisplacement, Map<Rectangle, Vector2> otherBoxes, BytePair tempNormal,
                                                                            Rectangle tempSum,  Vector2 tempRayHit){
        if(otherBoxes == null || otherBoxes.isEmpty())return otherBoxes != null ? otherBoxes : new HashMap<>();
        if(tempNormal == null)tempNormal = new BytePair();
        if(tempSum == null)tempSum = new Rectangle();
        if(tempRayHit == null)tempRayHit = new Vector2();
        Iterator<Map.Entry<Rectangle, Vector2>> itr = otherBoxes.entrySet().iterator();
        while (itr.hasNext()){
            Map.Entry<Rectangle, Vector2> entry = itr.next();
            if(!checkDynamicVsDynamic(firstBox, firstDisplacement, entry.getKey(), entry.getValue(), tempNormal, tempSum, tempRayHit)){
                itr.remove();
            }
        }
        return otherBoxes;
    }
    public static Map<Rectangle, Vector2> checkDynamicVsMultipleDynamic(Rectangle firstBox, Vector2 firstDisplacement, Map<Rectangle, Vector2> otherBoxes){
        return checkDynamicVsMultipleDynamic(firstBox, firstDisplacement, otherBoxes, null, null, null);
    }


}
