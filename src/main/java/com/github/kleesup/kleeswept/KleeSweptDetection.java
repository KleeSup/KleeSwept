package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Circle;
import com.github.kleesup.kleeswept.impl.SweptMagnitude;
import com.github.kleesup.kleeswept.response.SweptResult;
import com.github.kleesup.kleeswept.response.SimpleSweptResult;

/**
 * A class that contains all useful methods to detect collisions between AABB using swept collision detection.
 * NOTE: The math of all swept collision methods uses the CENTER of the AABBs.
 * Therefore, if you use methods such as {@link #checkAABBvsAABB(AABB, AABB, float, float)} be aware that <b>goalX</b> and <b>goalY</b> ALWAYS
 * need to be calculated from the CENTER of the AABBs!
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class KleeSweptDetection {

    /*
    Simple intersection checks
    */

    public static boolean doesPointIntersectAABB(AABB aabb, float pointX, float pointY){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        return pointX >= aabb.getX() &&
                pointX <= aabb.getMaxX() &&
                pointY >= aabb.getY() &&
                pointY <= aabb.getMaxY();
    }

    public static boolean doesAABBIntersectAABB(AABB aabb, AABB other){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        return aabb.getX() < other.getX() + other.getWidth() && aabb.getX() + aabb.getWidth() > other.getX() &&
                aabb.getY() < other.getY() + other.getHeight() && aabb.getY() + aabb.getHeight() > other.getY();
    }

    public static boolean doesAABBContainAABB(AABB aabb, AABB other){
        return ((other.getX() > aabb.getX() && other.getX() < aabb.getX() + aabb.getWidth()) &&
                (other.getMaxX() > aabb.getX() && other.getMaxX() < aabb.getX() + aabb.getWidth()))
                && ((other.getY() > aabb.getY() && other.getY() < aabb.getX() + aabb.getHeight()) &&
                (other.getMaxY() > aabb.getX() && other.getMaxY() < aabb.getX() + aabb.getHeight()));
    }

    public static boolean doesAABBContainCircle(AABB aabb, float circleX, float circleY, float radius){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        return (circleX - radius >= aabb.getX()) && (circleX + radius <= aabb.getX() + aabb.getWidth()) && (circleY - radius >= aabb.getY())
                && (circleY + radius <= aabb.getY() + aabb.getHeight());
    }

    public static boolean doesAABBContainCircle(AABB aabb, Circle circle){
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
     * @param resultWriteTo A temporary result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWriteTo A temporary point object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SimpleSweptResult doesRayIntersectAABB(float x, float y, float goalX, float goalY, AABB aabb, SimpleSweptResult resultWriteTo, Magnitude tempMagWriteTo){
        Magnitude magnitude = tempMagWriteTo != null ? tempMagWriteTo : new SweptMagnitude();
        KleeHelper.calculateMagnitude(x,y,goalX,goalY,magnitude);
        return doesRayIntersectAABB(x,y,magnitude,aabb,resultWriteTo);
    }
    public static SimpleSweptResult doesRayIntersectAABB(float x, float y, float goalX, float goalY, AABB aabb){
        return doesRayIntersectAABB(x,y,goalX,goalY,aabb,null, null);
    }

    /**
     * Does a swept collision test between a ray and an AABB.
     * @param x The x-coordinate of the ray (starting) position.
     * @param y The y-coordinate of the ray (starting) position.
     * @param magnitude The magnitude (direction) of the ray.
     * @param aabb The AABB to test against.
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SimpleSweptResult doesRayIntersectAABB(float x, float y, Magnitude magnitude, AABB aabb, SimpleSweptResult resultWriteTo){
        KleeHelper.paramRequireNonNull(magnitude, "Magnitude cannot be null!");
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        SimpleSweptResult response = resultWriteTo != null ? resultWriteTo : new SimpleSweptResult();
        response.reset();
        response.other = aabb;

        float lastEntry = Float.NEGATIVE_INFINITY;
        float firstExit = Float.POSITIVE_INFINITY;

        //magnitude should not be zero to avoid dividing by zero
        if(magnitude.getX() != 0){
            float t1 = (aabb.getX() - x) / magnitude.getX();
            float t2 = (aabb.getMaxX() - x) / magnitude.getX();
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (x <= aabb.getX() || x >= aabb.getMaxX()){
            return response;
        }
        if(magnitude.getY() != 0){
            float t1 = (aabb.getY() - y) / magnitude.getY();
            float t2 = (aabb.getMaxY() - y) / magnitude.getY();
            lastEntry = Math.max(lastEntry, Math.min(t1, t2));
            firstExit = Math.min(firstExit, Math.max(t1, t2));
        }else if (y <= aabb.getY() || y >= aabb.getMaxY()){
            return response;
        }

        //condition for a collision
        if(firstExit > lastEntry && firstExit > 0 && lastEntry < 1){
            response.x = x + magnitude.getX() * lastEntry;
            response.y = y + magnitude.getY() * lastEntry;

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
    public static SimpleSweptResult doesRayIntersectAABB(float x, float y, Magnitude magnitude, AABB aabb){
        return doesRayIntersectAABB(x,y,magnitude,aabb,null);
    }

    /*
    Swept AABB collision detection
    */

    /**
     * Does a swept collision test between two AABBs.
     * This method however uses the advanced check in which a so called 'sum-AABB' is created which adds both sizes of the AABBs together.
     * This new summed AABB will be positioned with the center at the center of the second AABB.
     * Then the swept test will no longer be between AABB 1 and 2 but between the main AABB (1) and the summed-AABB.
     * This step avoids the tunneling problem.
     * @param aabb The AABB to test for.
     * @param other The AABB to test against.
     * @param goalX The new x-coordinate at which the <b>aabb</b> wants to be located at (with the center!).
     * @param goalY The new y-coordinate at which the <b>aabb</b> wants to be located at (with the center!).
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWritTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @param fixHitPos Whether the {@link SweptResult#x} and {@link SweptResult#y} should be fixed to the actual hit position at the <b>other</b> AABB.
     *                  If this is {@code false}, the hit position in the response will not represent the hit position at the <b>other</b> AABB but the hit with the
     *                  created sum-AABB.
     * @return The result of the swept test.
     */
    public static SweptResult checkAABBvsAABB(AABB aabb, AABB other, float goalX, float goalY, SweptResult resultWriteTo,
                                              Magnitude tempMagWritTo, boolean fixHitPos){
        KleeHelper.paramRequireNonNull(aabb, "First AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        if(resultWriteTo == null)resultWriteTo = new SweptResult();
        AABB sum = KleeHelper.calculateSumAABB(aabb, other, resultWriteTo.sumAABB);
        SweptResult response = (SweptResult) doesRayIntersectAABB(aabb.getCenterX(), aabb.getCenterY(), goalX, goalY, sum, resultWriteTo, tempMagWritTo);
        response.aabb = aabb;
        response.other = other;
        response.sumAABB = sum;
        response.sumX = response.x;
        response.sumY = response.y;
        if(fixHitPos){
            //does a second collision test with the original other AABB to fix the hit-x/y
            SweptResult fixed = (SweptResult) doesRayIntersectAABB(aabb.getCenterX(), aabb.getCenterY(), goalX, goalY, other, new SweptResult(), tempMagWritTo);
            response.x = fixed.x;
            response.y = fixed.y;
            response.isHitPosFixed = true;
        }
        return response;
    }
    public static SweptResult checkAABBvsAABB(AABB aabb, AABB other, float goalX, float goalY, boolean fixHitPos){
        return checkAABBvsAABB(aabb, other, goalX, goalY, null, null, fixHitPos);
    }
    public static SweptResult checkAABBvsAABB(AABB aabb, AABB other, float goalX, float goalY){
        return checkAABBvsAABB(aabb, other, goalX, goalY, null, null, true);
    }

    /**
     * Does a swept collision test between two AABBs by taking in the velocity of the first <b>aabb</b>.
     * For more information check the documentation to {@link #checkAABBvsAABB(AABB, AABB, float, float, SweptResult, Magnitude, boolean)}.
     * @param aabb The AABB to test for.
     * @param other The AABB to test against.
     * @param velX The velocity-x of the first AABB.
     * @param velY The velocity-y of the first AABB.
     * @param resultWriteTo A temp result object where the outcome can be written into (Useful when less object creation is desired).
     * @param tempMagWritTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @param fixHitPos Whether the {@link SweptResult#x} and {@link SweptResult#y} should be fixed to the actual hit position at the <b>other</b> AABB.
     *                  If this is {@code false}, the hit position in the response will not represent the hit position at the <b>other</b> AABB but the hit with the
     *                  created sum-AABB.
     * @return The result of the swept collision test.
     */
    public static SweptResult checkAABBvsAABBWithVelocity(AABB aabb, AABB other, float velX, float velY, SweptResult resultWriteTo,
                                              Magnitude tempMagWritTo, boolean fixHitPos){
        return checkAABBvsAABB(aabb, other, aabb.getCenterX() + velX, aabb.getCenterY() + velY, resultWriteTo, tempMagWritTo, fixHitPos);
    }
    public static SweptResult checkAABBvsAABBWithVelocity(AABB aabb, AABB other, float velX, float velY, boolean fixHitPos){
        return checkAABBvsAABBWithVelocity(aabb,other,velX,velY,null,null,fixHitPos);
    }
    public static SweptResult checkAABBvsAABBWithVelocity(AABB aabb, AABB other, float velX, float velY){
        return checkAABBvsAABBWithVelocity(aabb,other,velX,velY,null,null,true);
    }

    /*
    Simple Swept AABB collision detection
    */

    /**
     * Does a simple swept AABB test between two AABBs.
     * Do NOT use this method for high velocity movements as this method does NOT fix the tunneling problem.
     * If that is a problem, use {@link #checkAABBvsAABB(AABB, AABB, float, float)} instead.
     * @param aabb The AABB to test for.
     * @param other The AABB to test against.
     * @param goalX The new x-coordinate at which the <b>aabb</b> wants to be located at (with the center!).
     * @param goalY The new y-coordinate at which the <b>aabb</b> wants to be located at (with the center!).
     * @param responseWriteTo A temp response object where the outcome can be written into (Useful when less object creation is desired).
     * @param magWriteTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SimpleSweptResult checkSimpleAABBvsAABB(AABB aabb, AABB other, float goalX, float goalY, SimpleSweptResult responseWriteTo, Magnitude magWriteTo){
        KleeHelper.paramRequireNonNull(aabb, "First AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        SimpleSweptResult response = doesRayIntersectAABB(aabb.getCenterX(), aabb.getCenterY(), goalX, goalY, other, responseWriteTo, magWriteTo);
        response.aabb = aabb;
        return response;
    }
    public static SimpleSweptResult checkSimpleAABBvsAABB(AABB aabb, AABB other, float goalX, float goalY){
        return checkSimpleAABBvsAABB(aabb,other,goalX,goalY,null,null);
    }

    /**
     * Does a swept collision test between two AABBs by taking in the velocity of the first <b>aabb</b>.
     * For more information check the documentation to {@link #checkAABBvsAABB(AABB, AABB, float, float, SweptResult, Magnitude, boolean)}.
     * @param aabb The AABB to test for.
     * @param other The AABB to test against.
     * @param velX The velocity-x of the first AABB.
     * @param velY The velocity-y of the first AABB.
     * @param responseWriteTo A temp response object where the outcome can be written into (Useful when less object creation is desired).
     * @param magWriteTo A temp magnitude object where the outcome can be written into (Useful when less object creation is desired).
     * @return The result of the swept collision test.
     */
    public static SimpleSweptResult checkSimpleAABBvsAABBWithVelocity(AABB aabb, AABB other, float velX, float velY, SimpleSweptResult responseWriteTo, Magnitude magWriteTo){
        KleeHelper.paramRequireNonNull(aabb, "First AABB cannot be null!");
        KleeHelper.paramRequireNonNull(other, "Second AABB cannot be null!");
        return checkSimpleAABBvsAABB(aabb, other, aabb.getCenterX() + velX, aabb.getCenterY() + velY, responseWriteTo, magWriteTo);
    }
    public static SimpleSweptResult checkSimpleAABBvsAABBWithVelocity(AABB aabb, AABB other, float velX, float velY){
        return checkSimpleAABBvsAABBWithVelocity(aabb, other, velX, velY, null, null);
    }

    /**
     * Does a quick collision test between two AABBs. However, this method expects that the first AABB has already been moved.
     * @param aabb The first AABB (this could be the one moving into the other).
     * @param other The other AABB (could be the one which the first one moves into).
     * @return The result of the collision test.
     */
    public static SimpleSweptResult checkSimpleAABBvsAABB(AABB aabb, AABB other){
        return checkSimpleAABBvsAABB(aabb,other,aabb.getCenterX(),aabb.getCenterY());
    }



}
