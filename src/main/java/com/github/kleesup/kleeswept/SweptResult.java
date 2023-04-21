package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;

/**
 * A container for a collision swept result between two AABBs.
 * Note that a returned response instance doesn't necessarily mean that there was a collision as {@link #isHit} can still be {@code false}.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.1
 * @since 1.0.0
 */
public class SweptResult {

    /** The AABB that was used for the collision. */
    public Rectangle aabb;
    /** The AABB which may have been collided upon. */
    public Rectangle other;
    /** Whether there was a collision. */
    public boolean isHit;
    /** The calculated hit-time. */
    public float time;
    /** The x-coordinate of the point at which the collision occurred. */
    public float x;
    /** The y-coordinate of the point at which the collision occurred. */
    public float y;
    /** Determines which side was hit horizontally (1 = right; -1 = left; 0 = none) */
    public float normalX;
    /** Determines which side was hit vertically (1 = top; -1 = bottom; 0 = none) */
    public float normalY;


    /** The stored sum of both colliders, saved for further collision response. */
    public Rectangle sumAABB;

    /** Represents the hit position with the {@link #sumAABB}. */
    public float sumX, sumY;

    /** Determines if the {@link #x} and {@link #y} where fixed and no longer represent {@link #sumX} and {@link #sumY}. */
    public boolean isHitPosFixed;

    public void reset(){
        aabb = null;
        other = null;
        isHit = false;
        normalX = 0;
        normalY = 0;
        sumAABB = null;
        isHitPosFixed = false;
    }
}