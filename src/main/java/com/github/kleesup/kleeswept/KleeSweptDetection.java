package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * A class that contains all useful methods to detect collisions between AABB using swept collision detection.
 * NOTE: The math of all swept collision methods uses the CENTER of the AABBs.
 * Therefore, if you use methods such as {@link #checkDynamicVsStatic(Rectangle, Rectangle, float, float)} be aware that <b>goalX</b> and <b>goalY</b> ALWAYS
 * need to be calculated from the CENTER of the AABBs!
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.1
 * @since 1.0.0
 */
public class KleeSweptDetection {

    private static final boolean DEFAULT_FIX_HIT_POS = true;

    private static final Comparator<SweptResult> comparator = new Comparator<SweptResult>() {
        @Override
        public int compare(SweptResult o1, SweptResult o2) {
            return Float.compare(o2.time, o1.time); //sort for smallest time first
        }
    };

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
     * @param resultWriteTo A temporary result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWriteTo A temporary point object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SweptResult doesRayIntersectAABB(float x, float y, float goalX, float goalY, Rectangle aabb, SweptResult resultWriteTo, Vector2 tempMagWriteTo){
        Vector2 magnitude = tempMagWriteTo != null ? tempMagWriteTo : new Vector2();
        KleeHelper.calculateMagnitude(x,y,goalX,goalY,magnitude);
        return doesRayIntersectAABB(x,y,magnitude,aabb,resultWriteTo);
    }
    public static SweptResult doesRayIntersectAABB(float x, float y, float goalX, float goalY, Rectangle aabb){
        return doesRayIntersectAABB(x,y,goalX,goalY,aabb,null, null);
    }

    /**
     * Does a swept collision test between a ray and an AABB.
     * @param x The x-coordinate of the ray (starting) position.
     * @param y The y-coordinate of the ray (starting) position.
     * @param magnitude The magnitude (size) of the ray.
     * @param aabb The AABB to test against.
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SweptResult doesRayIntersectAABB(float x, float y, Vector2 magnitude, Rectangle aabb, SweptResult resultWriteTo){
        KleeHelper.paramRequireNonNull(magnitude, "Magnitude cannot be null!");
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        SweptResult response = resultWriteTo != null ? resultWriteTo : new SweptResult();
        response.reset();
        response.other = aabb;

        float lastEntry = Float.NEGATIVE_INFINITY;
        float firstExit = Float.POSITIVE_INFINITY;

        //magnitude should not be zero to avoid dividing by zero
        if(magnitude.x != 0){
            float t1 = (aabb.getX() - x) / magnitude.x;
            float t2 = (getMaxX(aabb) - x) / magnitude.x;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (x <= aabb.getX() || x >= getMaxX(aabb)){
            return response;
        }
        if(magnitude.y != 0){
            float t1 = (aabb.getY() - y) / magnitude.y;
            float t2 = (getMaxY(aabb) - y) / magnitude.y;
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (y <= aabb.getY() || y >= getMaxY(aabb)){
            return response;
        }

        //condition for a collision
        if(firstExit > lastEntry && firstExit > 0 && lastEntry < 1){
            response.x = x + magnitude.x * lastEntry;
            response.y = y + magnitude.y * lastEntry;

            response.isHit = true;
            response.time = lastEntry;

            //calculating hit normal
            float dx = response.x - getCenterX(aabb);
            float dy = response.y - getCenterY(aabb);
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
    public static SweptResult doesRayIntersectAABB(float x, float y, Vector2 magnitude, Rectangle aabb){
        return doesRayIntersectAABB(x,y,magnitude,aabb,null);
    }



    /*
    Swept AABB collision detection
    - dynamic vs static
    */

    /**
     * Does a swept collision test between two AABBs.
     * This method however uses the advanced check in which a so called 'sum-AABB' is created which adds both sizes of the AABBs together.
     * This new summed AABB will be positioned with the center at the center of the second AABB.
     * Then the swept test will no longer be between AABB 1 and 2 but between the main AABB (1) and the summed-AABB.
     * This step avoids the tunneling problem.
     * @param aabb The AABB to test for.
     * @param other The AABB to test against.
     * @param displacementX The x-displacement of the <b>aabb</b> (e.g. the velocity-x value for the current frame).
     * @param displacementY The y-displacement of the <b>aabb</b> (e.g. the velocity-y value for the current frame).
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWritTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @param fixHitPos Whether the {@link SweptResult#x} and {@link SweptResult#y} should be fixed to the actual hit position at the <b>other</b> AABB.
     *                  If this is {@code false}, the hit position in the response will not represent the hit position at the <b>other</b> AABB but the hit with the
     *                  created sum-AABB.
     * @return The result of the swept test.
     */
    public static SweptResult checkDynamicVsStatic(Rectangle aabb, Rectangle other, float displacementX, float displacementY, SweptResult resultWriteTo,
                                               Vector2 tempMagWritTo, boolean fixHitPos){
        KleeHelper.paramRequireNonNull(aabb, "First AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        if(resultWriteTo == null)resultWriteTo = new SweptResult();
        Rectangle sum = KleeHelper.calculateSumAABB(aabb, other, resultWriteTo.sumAABB);
        float goalX = getCenterX(aabb) + displacementX;
        float goalY = getCenterY(aabb) + displacementY;
        SweptResult response = doesRayIntersectAABB(getCenterX(aabb), getCenterY(aabb), goalX, goalY, sum, resultWriteTo, tempMagWritTo);
        response.aabb = aabb;
        response.other = other;
        response.sumAABB = sum;
        response.sumX = response.x;
        response.sumY = response.y;
        if(fixHitPos){
            //does a second collision test with the original other AABB to fix the hit-x/y
            SweptResult fixed = doesRayIntersectAABB(getCenterX(aabb), getCenterY(aabb), goalX, goalY, other, new SweptResult(), tempMagWritTo);
            response.x = fixed.x;
            response.y = fixed.y;
            response.isHitPosFixed = true;
        }
        return response;
    }
    public static SweptResult checkDynamicVsStatic(Rectangle aabb, Rectangle other, float displacementX, float displacementY, boolean fixHitPos){
        return checkDynamicVsStatic(aabb, other, displacementX, displacementY, null, null, fixHitPos);
    }
    public static SweptResult checkDynamicVsStatic(Rectangle aabb, Rectangle other, float displacementX, float displacementY){
        return checkDynamicVsStatic(aabb, other, displacementX, displacementY, null, null, true);
    }

    /**
     * Does a swept collision test between an AABB and multiple other AABBs.
     * @param aabb The AABB to test for.
     * @param others The AABBs to test against.
     * @param displacementX The x-displacement of the <b>aabb</b> (e.g. the velocity-x value for the current frame).
     * @param displacementY The y-displacement of the <b>aabb</b> (e.g. the velocity-y value for the current frame).
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWriteTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @param fixHitPos Whether the {@link SweptResult#x} and {@link SweptResult#y} should be fixed to the actual hit position at the <b>other</b> AABB.
     *                  If this is {@code false}, the hit position in the response will not represent the hit position at the <b>other</b> AABB but the hit with the
     *                  created sum-AABB.
     * @param removeNoHit Whether collision results should be removed from the output if there was no collision detected.
     * @return A list of all collision results (sorted by hit-time, lowest first).
     */
    public static List<SweptResult> checkDynamicVsMultipleStatic(Rectangle aabb, Collection<Rectangle> others, float displacementX, float displacementY,
                                                        List<SweptResult> resultWriteTo, Vector2 tempMagWriteTo, boolean fixHitPos, boolean removeNoHit){
        KleeHelper.paramRequireNonNull(aabb, "First AABB cannot be null!");
        if(resultWriteTo != null)resultWriteTo.clear();
        if(others == null || others.isEmpty())return resultWriteTo == null ? new ArrayList<SweptResult>() : resultWriteTo;
        List<SweptResult> results = resultWriteTo == null ? new ArrayList<SweptResult>(others.size()) : resultWriteTo;
        if(tempMagWriteTo == null)tempMagWriteTo = new Vector2();
        for(Rectangle other : others){
            if(other == null)continue;
            SweptResult result = checkDynamicVsStatic(aabb, other, displacementX, displacementY, new SweptResult(), tempMagWriteTo, fixHitPos);
            if(removeNoHit && !result.isHit)continue;
            results.add(result);
        }
        Collections.sort(results, comparator);
        return results;
    }
    public static List<SweptResult> checkDynamicVsMultipleStatic(Rectangle aabb, Collection<Rectangle> others, float displacementX, float displacementY, boolean fixHitPos, boolean removeNoHit){
        return checkDynamicVsMultipleStatic(aabb, others, displacementX, displacementY, null, null, fixHitPos, removeNoHit);
    }
    public static List<SweptResult> checkDynamicVsMultipleStatic(Rectangle aabb, Collection<Rectangle> others, float displacementX, float displacementY, boolean removeNoHit){
        return checkDynamicVsMultipleStatic(aabb, others, displacementX, displacementY, null,null, true, removeNoHit);
    }
    public static List<SweptResult> checkDynamicVsMultipleStatic(Rectangle aabb, Collection<Rectangle> others, float displacementX, float displacementY){
        return checkDynamicVsMultipleStatic(aabb, others, displacementX, displacementY, null, null, true, true);
    }

    /*
    Swept AABB collision detection
    - dynamic vs dynamic
    */

    public static SweptResult checkDynamicVsDynamic(Rectangle aabb, float displacementX, float displacementY, Rectangle other, float otherDisplacementX, float otherDisplacementY,
                                                    SweptResult resultWriteTo, Vector2 tempMagWritTo, boolean fixHitPos){
        float disX = displacementX - otherDisplacementX;
        float disY = displacementY - otherDisplacementY;
        return checkDynamicVsStatic(aabb, other, disX, disY, resultWriteTo, tempMagWritTo, fixHitPos);
    }
    public static SweptResult checkDynamicVsDynamic(Rectangle aabb, float displacementX, float displacementY, Rectangle other, float otherDisplacementX, float otherDisplacementY,
                                                    boolean fixHitPos){
        return checkDynamicVsDynamic(aabb,displacementX,displacementY,other,otherDisplacementX,otherDisplacementY,null,null,fixHitPos);
    }
    public static SweptResult checkDynamicVsDynamic(Rectangle aabb, float displacementX, float displacementY, Rectangle other, float otherDisplacementX, float otherDisplacementY){
        return checkDynamicVsDynamic(aabb,displacementX,displacementY,other,otherDisplacementX,otherDisplacementY,DEFAULT_FIX_HIT_POS);
    }


}
